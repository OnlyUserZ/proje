package com.birgundegelecek.proje.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.service.LikeService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
	
	private final RedisTemplate<String, String> redisTemplate;
	private final LikeService likeService;
	
	@PostMapping("/toggle-like")
	@PreAuthorize("hasRole('ADMIN','USER')")
	public ResponseEntity<SorunLike> likeEkle(@RequestBody SorunLikeDTO dto) {
		
		SorunLike cevap = likeService.likeEkle(dto);
		
		return ResponseEntity.ok(cevap);
	}
	
	@GetMapping("/haftalik-en-cok-begenilenler")
	public List<Long> getHaftalikEnCokBegenilenSorunlar() {
	    Set<ZSetOperations.TypedTuple<String>> top =
	        redisTemplate.opsForZSet().reverseRangeWithScores("haftalik_like:", 0, 9); 
	    if (top == null) return List.of();

	    return top.stream()
	              .map(t -> Long.valueOf(t.getValue()))
	              .toList();
	}

	

}