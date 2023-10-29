package com.lions.redisall.mapper;

import com.lions.redisall.entity.VoucherOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * VoucherOrderMapper
 */
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    /**
     * 优惠卷订单表新增订单
     * @param voucherOrder DO
     * @return 是否新增成功
     */
    boolean insertVoucherOrder(VoucherOrder voucherOrder);
}
