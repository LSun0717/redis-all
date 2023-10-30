package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IVoucherOrderService
 * 业务逻辑层
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 秒杀下单
     * @param voucherId 秒杀优惠券id
     * @return 秒杀优惠卷订单id
     */
    Result flashSaleVoucherByLua(Long voucherId);

    /**
     * 创建优惠卷订单
     * @param voucherId 优惠券id
     * @return 优惠卷订单id
     */
    Result createVoucherOrderUnique(Long voucherId);

    /**
     * 解决事务失效，真实创建订单handler
     * @param voucherOrder 订单信息
     */
    void createVoucherOrderAscHandler(VoucherOrder voucherOrder);
}
