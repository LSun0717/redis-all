package com.lions.redisall;

import com.lions.redisall.utils.RedisIDGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class RedisAllApplicationTests {

    @Resource
    private RedisIDGenerator redisIDGenerator;

    private final ExecutorService executorService = Executors.newFixedThreadPool(500);

    /**
     * Redis分布式id生成测试
     * @throws InterruptedException
     */
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                Long orderId = redisIDGenerator.nextId("order");
                System.out.println("orderId:" + orderId);
            }
            countDownLatch.countDown();
        };

        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            executorService.submit(task);
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        long time = end - begin;
        System.out.println("执行时间：" + time);
    }
}
