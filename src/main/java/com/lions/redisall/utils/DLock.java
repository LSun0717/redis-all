package com.lions.redisall.utils;

/**
 * @Classname DLock
 * @Description 分布式锁接口
 * @Version 1.0.0
 * @Date 10/30/2023 2:33 PM
 * @Created by LIONS7
 */
public interface DLock {

    /**
     * 获取分布式锁
     * @param timeouts 超时时间
     * @return 是否获取成功
     */
    boolean tryLock(long timeouts);

    /**
     * 释放分布式锁
     */
    void unLock();
}
