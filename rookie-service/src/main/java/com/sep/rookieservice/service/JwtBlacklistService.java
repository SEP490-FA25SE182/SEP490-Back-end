package com.sep.rookieservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final StringRedisTemplate redis; // cấu hình Redis

    private String key(String jti) {
        return "bl:jwt:" + jti; // khóa blacklist
    }

    /** Đưa jti vào blacklist với TTL (giây) */
    public void blacklist(String jti, long ttlSeconds) {
        if (ttlSeconds <= 0) ttlSeconds = 1;
        redis.opsForValue().set(key(jti), "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String jti) {
        Boolean exists = redis.hasKey(key(jti));
        return exists != null && exists;
    }
}

