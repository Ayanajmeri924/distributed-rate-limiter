package com.ayan.ratelimiter;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
//import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
//@SpringBootTest
class FixedWindowRateLimiterTest {

    @Test
    void shouldAllowOnlyLimitRequestsUnderConcurrency() throws Exception {
        RateLimiter limiter = new FixedWindowRateLimiter(5, 60_000);

        int threads = 100;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger allowed = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await(); // wait for the starting gun
                    if (limiter.tryAcquire("user1").allowed()) {
                        allowed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown(); // fire: all 100 threads go at once
        done.await();      // wait until all are finished
        pool.shutdown();

        assertEquals(5, allowed.get());
    }
}