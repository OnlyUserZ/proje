package com.birgundegelecek.proje.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.exception.SorunBulunamadıException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.SorunLikeRepository;
import com.birgundegelecek.proje.repository.SorunRepository;
import com.birgundegelecek.proje.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class LikeService {
	
	private final RedisTemplate<String, String> redisTemplate;
	private final SorunLikeRepository sorunLikeRepository;
	private final UserRepository userRepository;
	private final SorunRepository sorunRepository;
	
	public Map<String, Object> likeEkle(SorunLikeDTO dto) {
	    User user = userRepository.findById(dto.getUser_id())
	            .orElseThrow(() -> new UserBulunamadıException("User Bulunamadı"));

	    Sorun sorun = sorunRepository.findById(dto.getSorun_id())
	            .orElseThrow(() -> new SorunBulunamadıException("Sorun Bulunamadı"));

	    Optional<SorunLike> var = sorunLikeRepository.findBySorunAndUser(sorun, user);

	    Map<String, Object> response = new HashMap<>();
	    String redisKey = "haftalık_likes"; 
	    String member = "sorun:" + sorun.getId();

	    if (var.isPresent()) {
	        
	        sorunLikeRepository.delete(var.get());
	        redisTemplate.opsForZSet().incrementScore(redisKey, member, -1); 
	        long toplam = sorunLikeRepository.countBySorun(sorun);
	        response.put("status", "UNLIKED");
	        response.put("totalLikes", toplam);
	    } else {
	       
	        SorunLike like = new SorunLike();
	        like.setSorun(sorun);
	        like.setUser(user);
	        sorunLikeRepository.save(like);
	        redisTemplate.opsForZSet().incrementScore(redisKey, member, 1); 
	        long toplam = sorunLikeRepository.countBySorun(sorun);
	        response.put("status", "LIKED");
	        response.put("totalLikes", toplam);
	    }

	    return response;
	}
	

}
