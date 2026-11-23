package com.ayan.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final RateLimiter limiter;

    public TestController(RateLimiter limiter) {
        this.limiter = limiter;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        RateLimitResult result = limiter.tryAcquire(ip);

        if (!result.allowed()) {
            return ResponseEntity.status(429).body("Too many requests");
        }
        return ResponseEntity.ok("OK, remaining: " + result.remaining());
    }
}