package com.lions.redisall.service.impl;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.SeckillVoucher;
import com.lions.redisall.entity.VoucherOrder;
import com.lions.redisall.mapper.SeckillVoucherMapper;
import com.lions.redisall.mapper.VoucherOrderMapper;
import com.lions.redisall.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.utils.RedisIDGenerator;
import com.lions.redisall.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
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
    @Transactional
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
        // 5.扣减库存(CAS，版本号机制)
        boolean updateSuccess = seckillVoucherMapper.updateStockByVoucherId(voucherId);
        if (!updateSuccess) {
            return Result.fail("优惠卷抢购失败");
        }
        // 6.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        Long voucherOrderId = redisIDGenerator.nextId("order");
        Long userId = UserHolder.getUser().getId();
        voucherOrder.setId(voucherOrderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        boolean insertFlag = voucherOrderMapper.insertVoucherOrder(voucherOrder);
        // 7.返回订单id
        return Result.ok(voucherOrderId);
    }
}
