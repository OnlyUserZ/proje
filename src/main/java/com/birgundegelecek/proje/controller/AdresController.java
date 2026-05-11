package com.birgundegelecek.proje.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.CustomUserDetails;
import com.birgundegelecek.proje.dto.AdresRequest;
import com.birgundegelecek.proje.dto.AdresResponse;
import com.birgundegelecek.proje.service.AdresService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/adres")
@RequiredArgsConstructor
@Slf4j

public class AdresController {
	
	private final AdresService adresService;
	
	@PostMapping("/ekle")
	public ResponseEntity<AdresResponse> adresEkle(@Valid @RequestBody AdresRequest adresRequest) {
		AdresResponse adresResponse = adresService.adresEkle(adresRequest);
		
		return ResponseEntity.ok(adresResponse);
		
	}
	
	@GetMapping("/getir/{adresId}")
	public ResponseEntity<AdresResponse> adresGetir(@AuthenticationPrincipal CustomUserDetails userDetails , @PathVariable Long adresId) {
		Long userId = userDetails.getId();
		
		AdresResponse adresResponse = adresService.adresGetir(adresId, userId);
		return ResponseEntity.ok(adresResponse);
	}

}
