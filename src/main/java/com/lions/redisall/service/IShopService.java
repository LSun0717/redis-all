package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IShopService
 * 业务逻辑层
 */
public interface IShopService extends IService<Shop> {

    /**
     * Cache：查询商铺信息
     * @param id 商铺id
     * @return 商铺详情
     */
    Result queryById(Long id);

    /**
     * 更新商铺信息
     * @param shop 新商户信息
     * @return 是否更新成功
     */
    Result updateShopInfo(Shop shop);
}
