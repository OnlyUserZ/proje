package com.birgundegelecek.proje.controller;

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
public class KategoriController {

    private final KategoriService kategoriService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KategoriDTO> kategoriEkle(@Valid @RequestBody KategoriDTO dto) {
        KategoriDTO eklenen = kategoriService.kategoriEkle(dto);
        return ResponseEntity.ok(eklenen);
    }
    
    @GetMapping
    
    public ResponseEntity<Page<KategoriDTO>> kategorilerGoster(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<KategoriDTO> list = kategoriService.kategorilerGoster(page, size);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/toplu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> kategorilerSil(@RequestBody List<Long> ids) {
        kategoriService.kategorilerSil(ids);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> kategoriGuncelle(@PathVariable Long id, @Valid @RequestBody KategoriDTO dto) {
        kategoriService.kategoriGuncelle(id, dto);
        return ResponseEntity.ok().build();
    }
}
