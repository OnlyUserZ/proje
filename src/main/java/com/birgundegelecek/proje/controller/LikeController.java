package com.birgundegelecek.proje.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.dto.SorunLikeDTO;
import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.service.LikeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
@Slf4j
public class LikeController {

    private final RedisTemplate<String, String> redisTemplate;
    private final LikeService likeService;

    @PostMapping("/toggle-like")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<SorunLike> likeEkle(@RequestBody @Valid SorunLikeDTO dto) {
        log.info("Like toggle isteği alındı.");
        log.debug("Like payload: {}", dto);

        SorunLike cevap = likeService.likeEkle(dto);

        if (cevap == null) {
            log.info("Like kaldırıldı. sorunId={}, userId={}", dto.getSorun_id(), dto.getUser_id());
        } else {
            log.info("Like eklendi. sorunId={}, userId={}", dto.getSorun_id(), dto.getUser_id());
        }

        return ResponseEntity.ok(cevap);
    }

    @GetMapping("/haftalik-en-cok-begenilenler")
    public List<Long> getHaftalikEnCokBegenilenSorunlar() {
        log.info("Haftalık en çok beğenilen sorunlar isteniyor.");

        Set<ZSetOperations.TypedTuple<String>> top =
                redisTemplate.opsForZSet().reverseRangeWithScores("haftalik_like:", 0, 9);

        if (top == null) {
            log.warn("Redis haftalik_like seti boş veya bulunamadı.");
            return List.of();
        }

        List<Long> sonuc = top.stream()
                .map(t -> Long.valueOf(t.getValue()))
                .toList();

        log.info("Haftalık en çok beğenilen sorunlar getirildi. Adet: {}", sonuc.size());
        log.debug("Sorun ID listesi: {}", sonuc);

        return sonuc;
    }
}
