package com.lions.redisall.mapper;

import com.lions.redisall.entity.VoucherOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 根据订单表中userId和voucherId同时查询，如果记录存在，说明已有优惠卷
     * @param userId 用户id
     * @param voucherId 优惠卷id
     * @return 满足条件的技术总数
     */
    int countByIdAndVId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
}
