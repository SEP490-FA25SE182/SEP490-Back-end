package com.sep.rookieservice.service;

public interface JwtBlacklistService {
    /** Đưa jti vào blacklist với TTL (giây) */
    void blacklist(String jti, long ttlSeconds);

    boolean isBlacklisted(String jti);
}
