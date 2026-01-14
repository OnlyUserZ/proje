package com.birgundegelecek.proje;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service 
@RequiredArgsConstructor 
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void blacklistToken(String jti, long expirationMillis) {
        redisTemplate.opsForValue().set(PREFIX + jti, "true", Duration.ofMillis(expirationMillis));
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }
}

