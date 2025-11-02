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
import com.birgundegelecek.proje.exception.SorunLikeBulunamadıException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.SorunLikeRepository;
import com.birgundegelecek.proje.repository.SorunRepository;
import com.birgundegelecek.proje.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.var;

@Service
@RequiredArgsConstructor

public class LikeService {
	
	private final RedisTemplate<String, String> redisTemplate;
	private final SorunLikeRepository sorunLikeRepository;
	private final UserRepository userRepository;
	private final SorunRepository sorunRepository;
	
	public SorunLike likeEkle(SorunLikeDTO dto) {
	    User user = userRepository.findById(dto.getUser_id())
	            .orElseThrow(() -> new UserBulunamadıException("User Bulunamadı"));

	    Sorun sorun = sorunRepository.findById(dto.getSorun_id())
	            .orElseThrow(() -> new SorunBulunamadıException("Sorun Bulunamadı"));

	    boolean var = sorunLikeRepository.existsBySorunAndUser(sorun, user);
	    
	    if(var == true) {
	    	sorunLikeRepository.deleteBySorunAndUser(sorun, user);
	    	sorun.setLikeToplam(sorun.getLikeToplam() - 1);
	    	return null;
	    }
	    
	    SorunLike sorunLike = new SorunLike();
	    sorunLike.setSorun(sorun);
	    sorunLike.setUser(user);
	    
	    sorun.setLikeToplam(sorun.getLikeToplam() + 1);
	    return sorunLikeRepository.save(sorunLike);
	
	}
	

}
