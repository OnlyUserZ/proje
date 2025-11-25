package com.birgundegelecek.proje.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.KategoriDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KategoriService {

    private final KategoriRepository kategoriRepository;

    @Transactional
    public KategoriDTO kategoriEkle(KategoriDTO dto) {
        log.info("Yeni kategori ekleniyor: {}", dto.getAd());

        Kategori kategori = new Kategori();
        kategori.setAd(dto.getAd());

        Kategori kaydedilen = kategoriRepository.save(kategori);
        log.info("Kategori kaydedildi. ID: {}", kaydedilen.getId());

        KategoriDTO cevap = new KategoriDTO();
        cevap.setAd(kaydedilen.getAd());
        cevap.setId(kaydedilen.getId());

        return cevap;
    }

    public Page<KategoriDTO> kategorilerGoster(int page, int size) {
        log.info("Kategoriler listeleniyor. page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("ad").ascending());
        Page<Kategori> kategoriPage = kategoriRepository.findAll(pageable);

        log.debug("Kategori sayfa bilgisi: toplam={}", kategoriPage.getTotalElements());

        return kategoriPage.map(k -> {
            KategoriDTO dto = new KategoriDTO();
            dto.setAd(k.getAd());
            dto.setId(k.getId());
            return dto;
        });
    }

    @Transactional
    public void kategorilerSil(List<Long> ids) {
        log.info("Toplu kategori silme başlatıldı. Silinecek ID sayısı: {}", ids.size());
        log.debug("Kategori silinecek ID listesi: {}", ids);

        List<Long> bulunmayan = ids.stream()
                .filter(id -> !kategoriRepository.existsById(id))
                .toList();

        if (!bulunmayan.isEmpty()) {
            log.warn("Silinmek istenen fakat bulunmayan kategori ID'leri: {}", bulunmayan);
            throw new KategoriBulunamadıException("Bulunamayan Kategori ID'leri: " + bulunmayan);
        }

        kategoriRepository.deleteAllById(ids);
        log.info("Toplu kategori silme tamamlandı.");
    }

    @Transactional
    public void kategoriGuncelle(Long id, KategoriDTO dto) {
        log.info("Kategori güncelleniyor. ID: {}", id);
        log.debug("Kategori yeni verileri: {}", dto);

        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Kategori bulunamadı. ID: {}", id);
                    return new KategoriBulunamadıException("Kategori Bulunamadı");
                });

        kategori.setAd(dto.getAd());
        kategoriRepository.save(kategori);

        log.info("Kategori güncelleme tamamlandı. ID: {}", id);
    }
}
