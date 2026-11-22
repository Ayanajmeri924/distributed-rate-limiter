package com.ayan.ratelimiter;

public interface RateLimiter {
    RateLimitResult tryAcquire(String key);
}
