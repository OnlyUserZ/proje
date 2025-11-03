package com.birgundegelecek.proje.service;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.birgundegelecek.proje.event.LikeEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void likeEventAl(LikeEvent event) {
        String key = "haftalik_like:" + event.getSorunId();

        if (event.isLikeEklendi()) {
            redisTemplate.opsForValue().increment(key); 
        } else {
            redisTemplate.opsForValue().decrement(key); 
        }
    }
}
