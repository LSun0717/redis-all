package com.lions.redisall.mapper;

import com.lions.redisall.entity.SeckillVoucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * SeckillVoucherMapper
 */
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {
    /**
     * 根据优惠卷id获取秒杀优惠卷信息
     * @param id 秒杀优惠卷id
     * @return SeckillVoucher DO
     */
    SeckillVoucher selectById(Long id);

    /**
     * 扣减库存，版本号+CAS机制
     * @param id 秒杀优惠卷id
     * @return 是否扣减成功
     */
    boolean updateStockByVoucherId(@Param("id") Long id);

}
