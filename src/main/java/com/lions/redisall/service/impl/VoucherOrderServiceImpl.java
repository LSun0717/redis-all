package com.lions.redisall.service.impl;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.SeckillVoucher;
import com.lions.redisall.entity.VoucherOrder;
import com.lions.redisall.mapper.SeckillVoucherMapper;
import com.lions.redisall.mapper.VoucherOrderMapper;
import com.lions.redisall.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.utils.RedisIDGenerator;
import com.lions.redisall.utils.SimpleDLock;
import com.lions.redisall.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.*;

import static com.lions.redisall.utils.RedisConstants.LOCK_ORDER_KEY;

@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    // 分布式id生成器
    @Resource
    private RedisIDGenerator redisIDGenerator;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // Redisson客户端
    @Resource
    private RedissonClient redissonClient;

    // Lua脚本静态初始化
    private static final DefaultRedisScript<Long> VALID_ORDER_SCRIPT;
    static {
        VALID_ORDER_SCRIPT = new DefaultRedisScript<>();
        VALID_ORDER_SCRIPT.setLocation(new ClassPathResource("validVoucher.lua"));
        VALID_ORDER_SCRIPT.setResultType(Long.class);
    }

    // 阻塞式消息队列
    private static final BlockingQueue<VoucherOrder> orderBlockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    // 线程池工作队列
    private static final BlockingQueue<Runnable> ORDER_WORK_QUEUE = new ArrayBlockingQueue<>(1024 * 1024);
    // 订单消息消费线程池
    private static final ExecutorService Voucher_ORDER_EXECUTOR = new ThreadPoolExecutor(
            20,
            40,
            5,
            TimeUnit.MINUTES,
            ORDER_WORK_QUEUE
    );

    /**
     * 优惠卷秒杀，异步版本
     * 秒杀类型优惠卷下单，使用Set防止重复订单，使用INCR扣减库存与防止超卖
     * 异步下单
     * @param voucherId 秒杀优惠卷id
     * @return 秒杀优惠卷订单id
     */
    @Override
    public Result flashSaleVoucherByLua(Long voucherId) {
        // 1.执行Lua脚本，判断能否成功下单独
        Long userId = UserContext.getUser().getId();
        Long isValidOrder = stringRedisTemplate.execute(
                VALID_ORDER_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString()
        );
        // 2.判断能否成功下单独
        // 2.1.不为0，代表不满足购买条件
        if (isValidOrder.intValue() != 0) {
            return Result.fail(isValidOrder == 1 ? "库存不足" : "不能重复下单");
        }
        // 2.2.为0，异步保存下单信息
        VoucherOrder voucherOrder = new VoucherOrder();
        Long voucherOrderId = redisIDGenerator.nextId("order");
        voucherOrder.setId(voucherOrderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        orderBlockingQueue.add(voucherOrder);
        // 3.返回订单id
        return Result.ok(voucherOrderId);
    }

    /**
     * 当前类构造完成，启动线程池
     */
    @PostConstruct
    private void init() {
        Voucher_ORDER_EXECUTOR.submit(new VoucherOrderTask());
    }

    /**
     * 订单消息消费任务
     */
    private class VoucherOrderTask implements Runnable {

        // 轮询获取阻塞队列中的订单
        @Override
        public void run() {
            while (true) {
                try {
                    VoucherOrder voucherOrder = orderBlockingQueue.take();
                    createVoucherOrderAsc(voucherOrder);
                } catch (Exception e) {
                    log.error("处理订单异常");
                }
            }
        }
    }

    // 解决事务失效
    @Resource
    private IVoucherOrderService proxy;

    /**
     * 创建订单 校验
     * @param voucherOrder 优惠卷订单
     */
    public void createVoucherOrderAsc(VoucherOrder voucherOrder) {
        // 构建分布式锁
        Long userId = voucherOrder.getId();
        RLock lock = redissonClient.getLock(LOCK_ORDER_KEY + userId);
        // 获取锁
        boolean lockSuccess = lock.tryLock();
        if (!lockSuccess) {
            log.error("优惠卷一人仅限一张");
            return;
        }
        try {
            // 创建订单
            proxy.createVoucherOrderAscHandler(voucherOrder);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 真实创建订单的handler
     * @param voucherOrder 优惠卷订单
     */
    @Transactional
    @Override
    public void createVoucherOrderAscHandler(VoucherOrder voucherOrder) {
        // 一人一单校验
        Long userId = voucherOrder.getId();
        Long voucherId = voucherOrder.getVoucherId();
        int count = voucherOrderMapper.countByIdAndVId(userId, voucherId);
        if (count > 0) {
            log.error("优惠卷一人仅限一张");
        }
        // 扣减库存(CAS，版本号机制)
        boolean updateSuccess = seckillVoucherMapper.updateStockByVoucherId(voucherId);
        if (!updateSuccess) {
            log.error("优惠卷抢购失败");
        }
        // .创建订单
        voucherOrderMapper.insertVoucherOrder(voucherOrder);
    }

    /**
     * 优惠卷秒杀，同步版本
     * 秒杀类型优惠卷下单，使用乐观锁解决超卖问题
     * @param voucherId 秒杀优惠卷id
     * @return 秒杀优惠卷订单id
     */
    public Result flashSaleVoucherByJava(Long voucherId) {
        // 1.查询优惠券
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        // 2.判断秒杀是否开启
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("优惠卷秒杀活动尚未开始，请关注最新动态");
        }
        // 3.判断秒杀是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("优惠卷秒杀活动已经结束，请关注最新动态");
        }
        // 4.判断库存是否充足
        if (seckillVoucher.getStock() < 1) {
            return Result.fail("优惠卷已经被抢完啦！欢迎下次参与");
        }

        // 5.Redis分布式锁，防止并发创建
        Long userId = UserContext.getUser().getId();
        SimpleDLock simpleDLock = new SimpleDLock("order:" + userId);
        boolean lockSuccess = simpleDLock.tryLock(1200);
        if (!lockSuccess) {
            return Result.fail("不允许重复下单");
        }
        try {
            // 6 解决事务注解失效问题
            IVoucherOrderService currentProxy = (IVoucherOrderService) AopContext.currentProxy();
            // 7 创建订单
            Result voucherOrderId = currentProxy.createVoucherOrderUnique(voucherId);
            // 8.返回订单id
            return Result.ok(voucherOrderId);
        } finally {
            simpleDLock.unLock();
        }
    }

    /**
     * 创建优惠卷订单，结合一人一单校验
     * @param voucherId 优惠卷id
     * @return 优惠卷订单id
     */
    @Transactional
    @Override
    public Result createVoucherOrderUnique(Long voucherId) {
        // 7.1一人一单校验
        Long userId = UserContext.getUser().getId();
        int count = voucherOrderMapper.countByIdAndVId(userId, voucherId);
        if (count > 0) {
            return Result.fail("优惠卷一人仅限一张");
        }
        // 7.2扣减库存(CAS，版本号机制)
        boolean updateSuccess = seckillVoucherMapper.updateStockByVoucherId(voucherId);
        if (!updateSuccess) {
            return Result.fail("优惠卷抢购失败");
        }
        // 7.3.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        Long voucherOrderId = redisIDGenerator.nextId("order");
        voucherOrder.setId(voucherOrderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        boolean insertFlag = voucherOrderMapper.insertVoucherOrder(voucherOrder);
        return Result.ok(voucherOrderId);
    }
}
