package com.birgundegelecek.proje.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.KategoriDTO;
import com.birgundegelecek.proje.service.KategoriService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kategori")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<KategoriDTO> kategoriEkle(@RequestBody KategoriDTO dto) {
        KategoriDTO eklenen = kategoriService.kategoriEkle(dto);
        return ResponseEntity.ok(eklenen);
    }

    @GetMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Page<KategoriDTO>> kategorilerGoster(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<KategoriDTO> list = kategoriService.kategorilerGoster(page, size);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/toplu")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> kategorilerSil(@RequestBody List<Long> ids) {
        kategoriService.kategorilerSil(ids);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> kategoriGuncelle(@PathVariable Long id, @RequestBody KategoriDTO dto) {
        kategoriService.kategoriGuncelle(id, dto);
        return ResponseEntity.ok().build();
    }
}
