package com.ayan.ratelimiter;

public record RateLimitResult(boolean allowed, long remaining) {}
