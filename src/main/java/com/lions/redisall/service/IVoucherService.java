package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IVoucherService
 * 业务逻辑层
 */
public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
