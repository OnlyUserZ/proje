package com.birgundegelecek.proje.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.event.LikeEvent;
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
	private final ApplicationEventPublisher publisher;
	
	public SorunLike likeEkle(SorunLikeDTO dto) {
	    User user = userRepository.findById(dto.getUser_id())
	            .orElseThrow(() -> new UserBulunamadıException("User Bulunamadı"));

	    Sorun sorun = sorunRepository.findById(dto.getSorun_id())
	            .orElseThrow(() -> new SorunBulunamadıException("Sorun Bulunamadı"));

	    boolean varmi = sorunLikeRepository.existsBySorunAndUser(sorun, user);
	    
	    if(varmi == true) {
	    	sorunLikeRepository.deleteBySorunAndUser(sorun, user);
	    	sorun.setLikeToplam(sorun.getLikeToplam() - 1);	    	
	    	publisher.publishEvent(new LikeEvent(this , sorun.getId(), false)); 
	    	return null;
	    }
	    
	    SorunLike sorunLike = new SorunLike();
	    sorunLike.setSorun(sorun);
	    sorunLike.setUser(user);
	    
	    
	    sorun.setLikeToplam(sorun.getLikeToplam() + 1);
	    publisher.publishEvent(new LikeEvent(this , sorun.getId(), true));
	    return sorunLikeRepository.save(sorunLike);
	
	}
	
	

}
