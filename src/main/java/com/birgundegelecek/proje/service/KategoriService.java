package com.birgundegelecek.proje.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.birgundegelecek.proje.dto.KategoriRequest;
import com.birgundegelecek.proje.dto.KategoriResponse;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KategoriService {

    private final KategoriRepository kategoriRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY = "kategori:all";

   
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public KategoriResponse create(KategoriRequest request) {

        log.info("Kategori oluşturma isteği alındı: {}", request.getAd());

        if (kategoriRepository.existsByAdAndDeletedFalse(request.getAd())) {
            log.warn("Kategori zaten mevcut: {}", request.getAd());
            throw new RuntimeException("Kategori zaten var");
        }

        Kategori kategori = new Kategori();
        kategori.setAd(request.getAd());

        kategoriRepository.save(kategori);

        log.info("Kategori oluşturuldu id={}", kategori.getId());

        redisTemplate.delete(CACHE_KEY); 

        return new KategoriResponse(
                kategori.getId(),
                kategori.getAd()
        );
    }

   
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<KategoriResponse> getAll() {

        try {

            String cached = redisTemplate.opsForValue().get(CACHE_KEY);

            if (cached != null) {
                log.info("Kategoriler Redis cache'den alındı");

                return objectMapper.readValue(
                        cached,
                        new TypeReference<List<KategoriResponse>>() {}
                );
            }

            log.info("Kategoriler DB'den çekiliyor");

            List<KategoriResponse> result = kategoriRepository.findAllActiveKategoris()
                    .stream()
                    .map(k -> new KategoriResponse(
                            k.getId(),
                            k.getAd()))
                    .collect(Collectors.toList());

            redisTemplate.opsForValue().set(
                    CACHE_KEY,
                    objectMapper.writeValueAsString(result)
            );

            return result;

        } catch (Exception e) {

            log.error("Kategori cache hatası", e);

            throw new RuntimeException(e);
        }
    }


    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public KategoriResponse getById(Long id) {

        log.info("Kategori getiriliyor id={}", id);

        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Kategori bulunamadı id={}", id);
                    return new KategoriBulunamadıException("Kategori bulunamadı");
                });

        return new KategoriResponse(
                kategori.getId(),
                kategori.getAd()
        );
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public KategoriResponse update(Long id, KategoriRequest request) {

        log.info("Kategori güncelleme id={}", id);

        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> new KategoriBulunamadıException("Kategori bulunamadı"));

        kategori.setAd(request.getAd());

        redisTemplate.delete(CACHE_KEY);

        log.info("Kategori güncellendi id={}", id);

        return new KategoriResponse(
                kategori.getId(),
                kategori.getAd()
        );
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {

        log.info("Kategori silme id={}", id);

        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> new KategoriBulunamadıException("Kategori bulunamadı"));

        kategoriRepository.delete(kategori);

        redisTemplate.delete(CACHE_KEY);

        log.info("Kategori soft delete edildi id={}", id);
    }
}