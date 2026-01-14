package com.birgundegelecek.proje.service;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.birgundegelecek.proje.event.LikeEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Async
    @EventListener
    public void likeEventAl(LikeEvent event) {
        String key = "haftalik_like:";

        if (event.isLikeEklendi()) {      
            redisTemplate.opsForZSet().incrementScore(key , String.valueOf(event.getSorunId()) , 1.0);
        } else {
        	redisTemplate.opsForZSet().incrementScore(key, String.valueOf(event.getSorunId()), -1.0);
        }
    }
    
}
