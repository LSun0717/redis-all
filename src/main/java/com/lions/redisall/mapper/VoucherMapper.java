package com.lions.redisall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lions.redisall.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * VoucherMapper
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    /**
     * 查询商铺开售的优惠卷信息
     * @param shopId 商铺id
     * @return 优惠卷列表
     */
    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
