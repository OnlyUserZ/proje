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
		
	    Long count = redisTemplate.opsForValue().increment(key);
	    if(count == 1) {
	    	redisTemplate.expire(key, Duration.ofSeconds(60));
	    }
	    return count <= MAX_REQUEST;
	}

}
