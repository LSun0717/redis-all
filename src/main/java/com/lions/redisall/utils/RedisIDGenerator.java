package com.lions.redisall.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @Classname RedisIDGenerator
 * @Description 基于Redis实现分布式id
 * @Version 1.0.0
 * @Date 10/29/2023 2:28 PM
 * @Created by LIONS7
 */
@Component
public class RedisIDGenerator {
    // 当前时间戳
    private static final Long BEGIN_TIMESTAMP = 1698537600L;
    // 时间戳位长
    private static final int COUNT_BITS_LEN = 32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成分布式ID
     * @param keyPrefix Key前缀
     * @return ID
     */
    public Long nextId(String keyPrefix) {
        // 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;
        // 生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long sequenceNum = stringRedisTemplate.opsForValue().increment("incr:" + keyPrefix + ":" + date);
        // 拼接
        long id = timeStamp << COUNT_BITS_LEN | sequenceNum;
        return id;
    }

}
