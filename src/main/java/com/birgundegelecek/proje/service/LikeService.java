package com.birgundegelecek.proje.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.exception.SorunBulunamadıException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.SorunLikeRepository;
import com.birgundegelecek.proje.repository.SorunRepository;
import com.birgundegelecek.proje.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SorunLikeRepository sorunLikeRepository;
    private final UserRepository userRepository;
    private final SorunRepository sorunRepository;
   

    
    @Transactional
    public SorunLike likeEkle(SorunLikeDTO dto) {

        log.info("Like toggle işlemi başlatıldı. sorunId={}, userId={}", dto.getSorun_id(), dto.getUser_id());
        
        
        User user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> {
                    log.error("User bulunamadı. userId={}", dto.getUser_id());
                    return new UserBulunamadıException("User Bulunamadı");
                });

        Sorun sorun = sorunRepository.findById(dto.getSorun_id())
                .orElseThrow(() -> {
                    log.error("Sorun bulunamadı. sorunId={}", dto.getSorun_id());
                    return new SorunBulunamadıException("Sorun Bulunamadı");
                });

        boolean varmi = sorunLikeRepository.existsBySorunAndUser(sorun, user);

        if (varmi) {
            log.info("Like zaten mevcut → kaldırılıyor. sorunId={}, userId={}", dto.getSorun_id(), dto.getUser_id());
            sorunLikeRepository.deleteBySorunAndUser(sorun, user);

            sorun.setLikeToplam(sorun.getLikeToplam() - 1);
          

            return null;
        }

        log.info("Yeni like ekleniyor. sorunId={}, userId={}", dto.getSorun_id(), dto.getUser_id());

        SorunLike sorunLike = new SorunLike();
        sorunLike.setSorun(sorun);
        sorunLike.setUser(user);

        sorun.setLikeToplam(sorun.getLikeToplam() + 1);
        

        SorunLike saved = sorunLikeRepository.save(sorunLike);
        log.debug("Like kaydedildi: {}", saved);

        return saved;
        
        }
    }