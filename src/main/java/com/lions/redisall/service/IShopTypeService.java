package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopTypeService extends IService<ShopType> {

    /**
     * Cache + 获取商铺类型
     * @return 商铺类型List
     */
    Result getTypeList();
}
