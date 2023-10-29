package com.lions.redisall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.ShopType;
import com.lions.redisall.mapper.ShopTypeMapper;
import com.lions.redisall.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopTypeMapper shopTypeMapper;
    /**
     * Cache + 获取商铺类型
     *
     * @return 商铺类型List
     */
    @Override
    public Result getTypeList() {
        String shopTypeJsonStr = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + "type");
        if (StrUtil.isNotBlank(shopTypeJsonStr)) {
            List<ShopType> shopTypes = JSONUtil.toList(shopTypeJsonStr, ShopType.class);
            return Result.ok(shopTypes);
        }
        List<ShopType> shopTypes = shopTypeMapper.queryAll();
        if (shopTypes == null) {
            return Result.fail("商铺类型不存在");
        }
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + "type", JSONUtil.toJsonStr(shopTypes));
        return Result.ok(shopTypes);
    }
}
