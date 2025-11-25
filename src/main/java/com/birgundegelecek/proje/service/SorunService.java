package com.birgundegelecek.proje.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.exception.SorunBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.birgundegelecek.proje.repository.SorunRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SorunService {

    private final SorunRepository sorunRepository;
    private final KategoriRepository kategoriRepository;

    @Transactional
    public SorunDTO sorunEkle(SorunDTO dto) {
        log.info("Yeni sorun ekleniyor. baslik={}", dto.getBaslik());
        log.debug("Sorun payload: {}", dto);

        Kategori kategori = kategoriRepository.findById(dto.getKategoriId())
                .orElseThrow(() -> {
                    log.error("Kategori bulunamadı. kategoriId={}", dto.getKategoriId());
                    return new KategoriBulunamadıException("Kategori Bulunamadı");
                });

        Sorun sorun = new Sorun();
        sorun.setBaslik(dto.getBaslik());
        sorun.setCozum(dto.getCozum());
        sorun.setSorun(dto.getSorun());
        sorun.setKategori(kategori);

        Sorun kaydedilen = sorunRepository.save(sorun);

        log.info("Sorun başarıyla kaydedildi. sorunId={}", kaydedilen.getId());

        SorunDTO responseDto = new SorunDTO();
        responseDto.setBaslik(kaydedilen.getBaslik());
        responseDto.setCozum(kaydedilen.getCozum());
        responseDto.setSorun(kaydedilen.getSorun());
        responseDto.setKategoriId(kaydedilen.getKategori().getId());

        return responseDto;
    }

    public Page<SorunDTO> sorunlarGoster(Long kategoriId, int page, int size) {

        log.info("Kategoriye göre sorunlar listeleniyor. kategoriId={}", kategoriId);

        if (!kategoriRepository.existsById(kategoriId)) {
            log.warn("Sorun listelenmek isteniyor fakat kategori bulunamadı. kategoriId={}", kategoriId);
            throw new KategoriBulunamadıException("Kategori bulunamadı");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("baslik").ascending());
        Page<Sorun> sorunPage = sorunRepository.findByKategoriId(kategoriId, pageable);

        log.info("Sorunlar listelendi. toplamSonuç={}", sorunPage.getTotalElements());

        return sorunPage.map(k -> {
            SorunDTO dto = new SorunDTO();
            dto.setBaslik(k.getBaslik());
            dto.setCozum(k.getCozum());
            dto.setSorun(k.getSorun());
            dto.setKategoriId(k.getKategori().getId());
            return dto;
        });
    }

    @Transactional
    public void sorunGuncelle(Long id, SorunDTO dto) {
        log.info("Sorun güncelleniyor. sorunId={}", id);
        log.debug("Güncelleme payload: {}", dto);

        Sorun sorun = sorunRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Güncellenmek istenen sorun bulunamadı. sorunId={}", id);
                    return new SorunBulunamadıException("Sorun Bulunamadı");
                });

        Kategori kategori = kategoriRepository.findById(dto.getKategoriId())
                .orElseThrow(() -> {
                    log.error("Güncelleme sırasında kategori bulunamadı. kategoriId={}", dto.getKategoriId());
                    return new KategoriBulunamadıException("Kategori Bulunamadı");
                });

        sorun.setBaslik(dto.getBaslik());
        sorun.setCozum(dto.getCozum());
        sorun.setSorun(dto.getSorun());
        sorun.setKategori(kategori);

        sorunRepository.save(sorun);
        log.info("Sorun başarıyla güncellendi. sorunId={}", id);
    }

    @Transactional
    public void topluSil(List<Long> ids) {
        log.info("Toplu sorun silme başlatıldı. adet={}", ids.size());
        log.debug("Silinmek istenen sorun ID listesi: {}", ids);

        List<Long> bulunmayan = ids.stream()
                .filter(id -> !sorunRepository.existsById(id))
                .toList();

        if (!bulunmayan.isEmpty()) {
            log.warn("Silinmek istenen fakat bulunmayan sorun ID'leri: {}", bulunmayan);
            throw new SorunBulunamadıException("Bulunamayan Sorun ID'leri: " + bulunmayan);
        }

        sorunRepository.deleteAllById(ids);

        log.info("Toplu sorun silme tamamlandı.");
    }

}
