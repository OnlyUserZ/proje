package com.birgundegelecek.proje.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.service.LikeService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
	
	private final LikeService likeService;
	
	@PostMapping("/toggle-like")
	public ResponseEntity<SorunLike> likeEkle(@RequestBody SorunLikeDTO dto) {
		
		SorunLike cevap = likeService.likeEkle(dto);
		
		return ResponseEntity.ok(cevap);
	}
	

}