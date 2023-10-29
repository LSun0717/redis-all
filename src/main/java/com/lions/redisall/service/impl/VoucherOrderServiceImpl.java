package com.lions.redisall.service.impl;

import com.lions.redisall.entity.VoucherOrder;
import com.lions.redisall.mapper.VoucherOrderMapper;
import com.lions.redisall.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

}
