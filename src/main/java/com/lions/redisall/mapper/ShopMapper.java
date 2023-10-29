package com.lions.redisall.mapper;

import com.lions.redisall.entity.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * ShopMapper
 */
public interface ShopMapper extends BaseMapper<Shop> {
    /**
     * 根据商铺id查询商铺信息
     * @param id 商铺id
     * @return 商铺DO
     */
    Shop selectById(Long id);

    /**
     * DB:商铺信息更新
     *
     * @param shop@return 是否更新成功
     */
    int updateById(Shop shop);
}
