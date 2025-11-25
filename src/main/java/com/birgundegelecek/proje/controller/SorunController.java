package com.birgundegelecek.proje.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.service.SorunService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/sorun")
@RequiredArgsConstructor
@Slf4j
public class SorunController {

    private final SorunService sorunService;

    @GetMapping("/kategori/{kategoriId}")
    public ResponseEntity<Page<SorunDTO>> getSorunlar(
            @PathVariable Long kategoriId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Kategoriye göre sorun listeleme isteği alındı. kategoriId={}, page={}, size={}", kategoriId, page, size);

        Page<SorunDTO> list = sorunService.sorunlarGoster(kategoriId, page, size);

        log.info("Sorun listeleme tamamlandı. Toplam sorun: {}", list.getTotalElements());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SorunDTO> createSorun(@Valid @RequestBody SorunDTO dto) {
        log.info("Sorun oluşturma isteği alındı.");
        log.debug("Sorun oluşturma payload: {}", dto);

        SorunDTO created = sorunService.sorunEkle(dto);

        log.info("Sorun başarıyla oluşturuldu. kategoriId={}", created.getKategoriId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSorun(@PathVariable Long id, @Valid @RequestBody SorunDTO dto) {
        log.info("Sorun güncelleme isteği alındı. sorunId={}", id);
        log.debug("Güncelleme payload: {}", dto);

        sorunService.sorunGuncelle(id, dto);

        log.info("Sorun başarıyla güncellendi. sorunId={}", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSorun(@PathVariable Long id) {
        log.info("Tekil sorun silme isteği alındı. sorunId={}", id);

        sorunService.topluSil(List.of(id));

        log.info("Sorun silindi. sorunId={}", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/toplu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSorunlar(@RequestBody List<Long> ids) {
        log.info("Toplu sorun silme isteği alındı. Silinecek kayıt sayısı={}", ids.size());
        log.debug("Silinecek sorun ID listesi: {}", ids);

        sorunService.topluSil(ids);

        log.info("Toplu sorun silme işlemi tamamlandı.");
        return ResponseEntity.ok().build();
    }
}
