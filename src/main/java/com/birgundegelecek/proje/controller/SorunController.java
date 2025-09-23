package com.birgundegelecek.proje.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.service.SorunService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sorun")
@RequiredArgsConstructor
public class SorunController {

    private final SorunService sorunService;

    @GetMapping("/kategori/{kategoriId}")
    public ResponseEntity<Page<SorunDTO>> getSorunlar(
            @PathVariable Long kategoriId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SorunDTO> list = sorunService.sorunlarGoster(kategoriId, page, size);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SorunDTO> createSorun(@RequestBody SorunDTO dto) {
        SorunDTO created = sorunService.sorunEkle(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSorun(@PathVariable Long id, @RequestBody SorunDTO dto) {
        sorunService.sorunGuncelle(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")  
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSorun(@PathVariable Long id) {
        sorunService.topluSil(List.of(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/toplu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSorunlar(@RequestBody List<Long> ids) {
        sorunService.topluSil(ids);
        return ResponseEntity.ok().build();
    }
}

