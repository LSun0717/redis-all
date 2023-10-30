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
import com.lions.redisall.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    @Resource
    private RedisIDGenerator redisIDGenerator;

    /**
     * 秒杀类型优惠卷下单，使用乐观锁解决超卖问题
     * @param voucherId 秒杀优惠卷id
     * @return 秒杀优惠卷订单id
     */
    @Override
    public Result flashSaleVoucher(Long voucherId) {
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
        Long userId = UserHolder.getUser().getId();
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
        Long userId = UserHolder.getUser().getId();
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
