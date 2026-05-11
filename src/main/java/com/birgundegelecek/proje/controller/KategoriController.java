package com.birgundegelecek.proje.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.KategoriRequest;
import com.birgundegelecek.proje.dto.KategoriResponse;
import com.birgundegelecek.proje.service.KategoriService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/kategoriler")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KategoriResponse> create(@Valid @RequestBody KategoriRequest request) {

        log.info("POST /api/kategoriler");

        return ResponseEntity.ok(
                kategoriService.create(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<KategoriResponse>> getAll() {

        log.info("GET /api/kategoriler");

        return ResponseEntity.ok(
                kategoriService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<KategoriResponse> getById(@PathVariable Long id) {

        log.info("GET /api/kategoriler/{}", id);

        return ResponseEntity.ok(
                kategoriService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KategoriResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody KategoriRequest request) {

        log.info("PUT /api/kategoriler/{}", id);

        return ResponseEntity.ok(
                kategoriService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("DELETE /api/kategoriler/{}", id);

        kategoriService.delete(id);

        return ResponseEntity.noContent().build();
    }
}