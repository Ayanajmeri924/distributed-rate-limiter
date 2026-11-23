package com.ayan.ratelimiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisFixedWindowRateLimiter implements RateLimiter {

    private static final int LIMIT = 5;
    private static final long WINDOW_SECONDS = 60;

    private final StringRedisTemplate redis;

    public RedisFixedWindowRateLimiter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        String redisKey = "rate:" + key;

        long count = redis.opsForValue().increment(redisKey);   // INCR

        if (count == 1) {
            redis.expire(redisKey, Duration.ofSeconds(WINDOW_SECONDS));   // EXPIRE
        }

        if (count <= LIMIT) {
            return new RateLimitResult(true, LIMIT - count);
        }
        return new RateLimitResult(false, 0);
    }
}