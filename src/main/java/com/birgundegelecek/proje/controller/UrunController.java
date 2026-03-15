package com.birgundegelecek.proje.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.dto.UrunRequest;
import com.birgundegelecek.proje.dto.UrunResponse;
import com.birgundegelecek.proje.CustomUserDetails;
import com.birgundegelecek.proje.service.UrunService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/urunler")
@RequiredArgsConstructor
public class UrunController {

    private final UrunService urunService;

 
    @GetMapping("/kategori/{id}")
    public ResponseEntity<Page<UrunResponse>> urunlerGoster(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int sayfa,
            @RequestParam(defaultValue = "10") int boyut,
            @RequestParam(defaultValue = "ad") String siralaAlan,
            @RequestParam(defaultValue = "asc") String siralaYon) {

        Sort sort = siralaYon.equalsIgnoreCase("asc")
                ? Sort.by(siralaAlan).ascending()
                : Sort.by(siralaAlan).descending();

        Pageable pageable = PageRequest.of(sayfa, boyut, sort);

        Page<UrunResponse> cevap =
                urunService.kategori_ile_urun_bul(id, pageable);

        return ResponseEntity.ok(cevap);
    }


   
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UrunResponse> urunEkle(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid UrunRequest request) {

        UrunResponse cevap =
                urunService.urunEkle(request, user.getId());

        return ResponseEntity.ok(cevap);
    }


  
    @PutMapping("/{urun_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UrunResponse> urunGuncelle(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long urun_id,
            @RequestBody @Valid UrunRequest request) {

        UrunResponse cevap =
                urunService.urunGuncelle(urun_id, user.getId(), request);

        return ResponseEntity.ok(cevap);
    }


  
    @DeleteMapping("/{urun_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> urunSil(
    		@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long urun_id) {

        urunService.urunSil(urun_id, user.getId());

        return ResponseEntity.noContent().build();
    }

}