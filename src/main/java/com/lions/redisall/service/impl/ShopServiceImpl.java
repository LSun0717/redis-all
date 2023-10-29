package com.lions.redisall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.Shop;
import com.lions.redisall.mapper.ShopMapper;
import com.lions.redisall.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopMapper shopMapper;

    /**
     * Cache query
     *
     * @param id 商铺id
     * @return 商铺详情
     */
    @Override
    public Result queryById(Long id) {
        String shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
        // 判断合法的缓存是否命中
        if (StrUtil.isNotBlank(shopJson)) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        // 判断缓存命中的是否为防护缓存穿透的null值
        if (shopJson != null) {
            return Result.fail("店铺信息不存在");
        }
        // 查询DB
        Shop shop = shopMapper.selectById(id);
        // DB中也不存在，防止缓存穿透，缓存空值，并设置较短的TTL
        if (shop == null) {
            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("店铺信息不存在");
        }
        // DB中存在，回种缓存
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop), RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok(shop);
    }

    /**
     * 更新商铺信息，同时删除缓存
     * 写DB与删除缓存必须是原子操作
     * @param shop 新商户信息
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public Result updateShopInfo(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("商铺id不能为空");
        }
        // 更新DB
        int i = shopMapper.updateById(shop);
        // 删除缓存中的脏数据
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
