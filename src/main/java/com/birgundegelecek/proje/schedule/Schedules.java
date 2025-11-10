package com.birgundegelecek.proje.schedule;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Schedules {

    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 59 23 ? * SUN")
    public void HaftalikLikeleriSil() {
        
    	Set<String> keys = redisTemplate.keys("haftalik_like:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("Haftalık like verileri silindi: " + keys.size());
        }
    }
}
