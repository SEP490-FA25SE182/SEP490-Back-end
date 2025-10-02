package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.service.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    private final StringRedisTemplate redis;

    private String key(String jti) {
        return "bl:jwt:" + jti;
    }

    @Override
    public void blacklist(String jti, long ttlSeconds) {
        if (ttlSeconds <= 0) ttlSeconds = 1;
        redis.opsForValue().set(key(jti), "1", Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isBlacklisted(String jti) {
        Boolean exists = redis.hasKey(key(jti));
        return exists != null && exists;
    }
}
