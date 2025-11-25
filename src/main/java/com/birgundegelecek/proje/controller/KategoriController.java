package com.birgundegelecek.proje.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.KategoriDTO;
import com.birgundegelecek.proje.service.KategoriService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kategori")
@RequiredArgsConstructor
@Slf4j
public class KategoriController {

    private final KategoriService kategoriService;

    @PostMapping("/ekle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KategoriDTO> kategoriEkle(@Valid @RequestBody KategoriDTO dto) {
        log.info("Kategori ekleme isteği alındı.");
        log.debug("Kategori ekleme payload: {}", dto);

        KategoriDTO eklenen = kategoriService.kategoriEkle(dto);

        log.info("Kategori başarıyla eklendi. ID: {}", eklenen.getId());
        return ResponseEntity.ok(eklenen);
    }

    @GetMapping("/goster")
    public ResponseEntity<Page<KategoriDTO>> kategorilerGoster(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Kategori listeleme isteği alındı. page={}, size={}", page, size);

        Page<KategoriDTO> list = kategoriService.kategorilerGoster(page, size);

        log.info("Kategori listeleme işlemi tamamlandı. Toplam kayıt: {}", list.getTotalElements());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/toplu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> kategorilerSil(@RequestBody List<Long> ids) {
        log.info("Toplu kategori silme isteği alındı. Silinecek ID sayısı: {}", ids.size());
        log.debug("Silinecek kategori ID'leri: {}", ids);

        kategoriService.kategorilerSil(ids);

        log.info("Toplu kategori silme işlemi tamamlandı.");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/guncelle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> kategoriGuncelle(@PathVariable Long id, @Valid @RequestBody KategoriDTO dto) {
        log.info("Kategori güncelleme isteği alındı. ID: {}", id);
        log.debug("Kategori güncelleme payload: {}", dto);

        kategoriService.kategoriGuncelle(id, dto);

        log.info("Kategori güncelleme işlemi tamamlandı. ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
