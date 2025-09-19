package com.birgundegelecek.proje.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Page<SorunDTO>> getSorunlar(
            @PathVariable Long kategoriId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SorunDTO> list = sorunService.sorunlarGoster(kategoriId, page, size);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<SorunDTO> createSorun(@RequestBody SorunDTO dto) {
        SorunDTO created = sorunService.sorunEkle(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> updateSorun(@PathVariable Long id, @RequestBody SorunDTO dto) {
        sorunService.sorunGuncelle(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> deleteSorun(@PathVariable Long id) {
        sorunService.topluSil(List.of(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/toplu")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> deleteSorunlar(@RequestBody List<Long> ids) {
        sorunService.topluSil(ids);
        return ResponseEntity.ok().build();
    }
}

