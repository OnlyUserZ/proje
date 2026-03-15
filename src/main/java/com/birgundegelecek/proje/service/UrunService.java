package com.birgundegelecek.proje.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.UrunRequest;
import com.birgundegelecek.proje.dto.UrunResponse;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.Urun;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.exception.UrunBulunamadiException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.birgundegelecek.proje.repository.UrunRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrunService {

    private final UrunRepository urunRepository;
    private final KategoriRepository kategoriRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UrunResponse urunEkle(UrunRequest urunRequest, Long user_id) {

        log.info("Urun ekleme işlemi başlatıldı. userId={}", user_id);

        Urun urun = new Urun();
        urun.setAd(urunRequest.getAd());
        urun.setAciklama(urunRequest.getAciklama());
        urun.setFiyat(urunRequest.getFiyat());
        urun.setStok(urunRequest.getStok());

        List<Long> kategori_ids = urunRequest.getKategori_ids();
        Set<Kategori> kategoriler = new HashSet<>();

        for (long id : kategori_ids) {
            Kategori kategori = kategoriRepository.findById(id)
                    .orElseThrow(() -> {
                    	
                        log.warn("Urun ekleme başarısız: kategori bulunamadı. kategoriId={}", id);
                        return new KategoriBulunamadıException("Kategori Bulunamadı id :" + id);
                    });
            kategoriler.add(kategori);
        }

        urun.setKategoriler(kategoriler);

        Urun cevap = urunRepository.save(urun);
        log.info("Urun başarıyla eklendi. urunId={}, userId={}", cevap.getId(), user_id);

        return new UrunResponse(
                cevap.getId(),
                cevap.getAd(),
                cevap.getFiyat(),
                cevap.getStok(),
                cevap.getAciklama(),
                cevap.getKategoriler()
        );
    }

    public Page<UrunResponse> kategori_ile_urun_bul(Long kategoriId, Pageable pageable) {

        log.info("Kategoriye göre urunler aranıyor. kategoriId={}", kategoriId);

        Page<UrunResponse> response = urunRepository
                .findByKategori_id(kategoriId, pageable)
                .map(u -> new UrunResponse(
                        u.getId(),
                        u.getAd(),
                        u.getFiyat(),
                        u.getStok(),
                        u.getAciklama(),
                        u.getKategoriler()
                ));

        log.info("Kategoriye göre urunler bulundu. kategoriId={}, toplam={}", kategoriId, response.getTotalElements());

        return response;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UrunResponse urunGuncelle(Long urun_id, Long user_id, UrunRequest request) {
    	
        log.info("Urun güncelleme işlemi başlatıldı. urunId={}, userId={}", urun_id, user_id);

        Urun urun = urunRepository.findById(urun_id)
                .orElseThrow(() -> {
                    log.warn("Urun güncelleme başarısız: urun bulunamadı. urunId={}", urun_id);
                    return new UrunBulunamadiException("Urun Bulunamadi");
                });

        urun.setAd(request.getAd());
        urun.setAciklama(request.getAciklama());
        urun.setFiyat(request.getFiyat());
        urun.setStok(request.getStok());

        List<Long> kategori_ids = request.getKategori_ids();
        Set<Kategori> kategoriler = new HashSet<>();

        for (long id : kategori_ids) {
            Kategori kategori = kategoriRepository.findById(id)
                    .orElseThrow(() -> {
                    	
                        log.warn("Urun güncelleme başarısız: kategori bulunamadı. kategoriId={}", id);
                        return new KategoriBulunamadıException("Kategori Bulunamadı id :" + id);
                    });
            kategoriler.add(kategori);
        }
        
        urun.setKategoriler(kategoriler);

        Urun cevap = urunRepository.save(urun);
        log.info("Urun başarıyla güncellendi. urunId={}, userId={}", urun_id, user_id);

        return new UrunResponse(
                cevap.getId(),
                cevap.getAd(),
                cevap.getFiyat(),
                cevap.getStok(),
                cevap.getAciklama(),
                cevap.getKategoriler()
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void urunSil(Long urun_id, Long user_id) {

        log.info("Urun silme işlemi başlatıldı. urunId={}, userId={}", urun_id, user_id);

        urunRepository.deleteById(urun_id);

        log.info("Urun başarıyla silindi. urunId={}, userId={}", urun_id, user_id);
    }
}