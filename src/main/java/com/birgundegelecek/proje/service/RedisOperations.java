package com.birgundegelecek.proje.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.AuthRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisOperations {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String AUTH_REQUEST = "login:user:";
    private static final int MAX_REQUEST = 4;

    public boolean allowLogin(AuthRequest request) {
        String key = AUTH_REQUEST + request.getUsername();

        try {
            Long count = redisTemplate.opsForValue().increment(key);

            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(60));
            }

            return count != null && count <= MAX_REQUEST;

        } catch (Exception e) {
            
            return true;
        }
    }
}

