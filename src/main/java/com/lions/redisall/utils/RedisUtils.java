package com.lions.redisall.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Classname RedisUtils
 * @Description Redis工具类
 * @Version 1.0.0
 * @Date 10/28/2023 9:41 PM
 * @Created by LIONS7
 */
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 构建缓存，真实过期
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    public void setWithTTL(String key, Object value, Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }

    /**
     * 构建缓存，逻辑过期
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit timeUnit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 应对缓存穿透工具类
     * @param keyPrefix
     * @param id
     * @param type
     * @param time
     * @param timeUnit
     * @param dbFallback
     * @return
     * @param <R>
     * @param <ID>
     */
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Long time, TimeUnit timeUnit, Function<ID, R> dbFallback) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        if (json != null) {
            return null;
        }

        R r = dbFallback.apply(id);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        this.setWithTTL(key, r, time, timeUnit);
        return r;
    }

    /**
     * 缓存击穿，逻辑过期解决方案
     * @param keyPrefix
     * @param id
     * @param type
     * @param time
     * @param timeUnit
     * @param dbFallback
     * @return
     * @param <R>
     * @param <ID>
     */
    public <R, ID> R queryWithLogicExpire(
            String keyPrefix, ID id, Class<R> type, Long time, TimeUnit timeUnit, Function<ID, R> dbFallback) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(json)) {
            // 缓存未命中
            return null;
        }
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);

        LocalDateTime expireTime = redisData.getExpireTime();
        // 如果未过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            return r;
        }
        String lockKey = RedisConstants.LOCK_SHOP_KEY + id;
        boolean flag = this.tryLock(lockKey);
        if (flag) {
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    R newRes = dbFallback.apply(id);
                    this.setWithTTL(key, newRes, time, timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unLock(key);
                }
            });
        }
        return r;
    }

    /**
     * 获取分布式锁
     * @param key Key
     * @return 是否加锁成功
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放分布式锁
     * @param key 锁的Key
     * @return 是否释放锁成功
     */
    private boolean unLock(String key) {
        Boolean flag = stringRedisTemplate.delete(key);
        return BooleanUtil.isTrue(flag);
    }
}
