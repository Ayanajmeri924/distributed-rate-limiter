package com.ayan.ratelimiter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisFixedWindowRateLimiter implements RateLimiter {

    private static final int LIMIT = 5;
    private static final long WINDOW_SECONDS = 60;

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;

    public RedisFixedWindowRateLimiter(StringRedisTemplate redis) {
        this.redis = redis;
        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("scripts/fixed_window.lua"));
        this.script.setResultType(Long.class);
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        String redisKey = "rate:" + key;

        Long count = redis.execute(script, List.of(redisKey), String.valueOf(WINDOW_SECONDS));

        if (count != null && count <= LIMIT) {
            return new RateLimitResult(true, LIMIT - count);
        }
        return new RateLimitResult(false, 0);
    }
}