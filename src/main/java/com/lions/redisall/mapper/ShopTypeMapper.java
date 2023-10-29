package com.lions.redisall.mapper;

import com.lions.redisall.entity.ShopType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * ShopTypeMapper
 */
public interface ShopTypeMapper extends BaseMapper<ShopType> {
    List<ShopType> queryAll();
}
