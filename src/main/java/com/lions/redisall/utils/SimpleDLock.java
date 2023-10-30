package com.lions.redisall.utils;

import cn.hutool.core.lang.UUID;
import com.lions.redisall.utils.DLock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Classname SimpleDLock
 * @Description TODO
 * @Version 1.0.0
 * @Date 10/30/2023 2:35 PM
 * @Created by LIONS7
 */
public class SimpleDLock implements DLock {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 锁的Key = 统一前缀KEY_PREFIX + 业务名称name
    private static final String KEY_PREFIX_LOCK = "lock:";
    private final String businessName;

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    // 静态加载Lua脚本
    private static final DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT;
    static {
        UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_LUA_SCRIPT.setLocation(new ClassPathResource("unlockDLock.lua"));
        UNLOCK_LUA_SCRIPT.setResultType(Long.class);
    }

    public SimpleDLock(String businessName) {
        this.businessName = businessName;
    }

    /**
     * 获取分布式锁
     *
     * @param timeouts 超时时间
     * @return 是否获取成功
     */
    @Override
    public boolean tryLock(long timeouts) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX_LOCK + businessName, threadId, timeouts, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * Java代码释放分布式锁
     */
    public void unLockByJava() {
        // 当前线程的名称
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 当前锁的持有者
        String lockId = stringRedisTemplate.opsForValue().get(KEY_PREFIX_LOCK + businessName);
        if (threadId.equals(lockId)) {
            stringRedisTemplate.delete(KEY_PREFIX_LOCK + businessName);
        }
    }

    /**
     * Lua脚本释放分布式锁
     */
    @Override
    public void unLock() {
        stringRedisTemplate.execute(
                UNLOCK_LUA_SCRIPT,
                Collections.singletonList(KEY_PREFIX_LOCK + businessName),
                ID_PREFIX + Thread.currentThread().getId());
    }
}
