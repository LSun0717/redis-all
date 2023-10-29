package com.lions.redisall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lions.redisall.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * VoucherMapper
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
