package com.ayan.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter {

    private final int limit;          // max requests allowed, e.g. 5
    private final long windowMillis;  // window length, e.g. 60_000 (1 minute)

    // one Window object per user
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
    }

    private static class Window {
        long startTime;
        int count;
    }

    @Override
    public RateLimitResult tryAcquire(String key) {
        long now = System.currentTimeMillis();

        Window w = windows.computeIfAbsent(key, k -> new Window());

        synchronized (w) {
            if (now - w.startTime >= windowMillis) {
                w.startTime = now;
                w.count = 0;
            }

            if (w.count < limit) {
                w.count++;
                return new RateLimitResult(true, limit - w.count);
            }

            return new RateLimitResult(false, 0);
        }
    }
}
