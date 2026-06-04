package com.birgundegelecek.proje.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.CustomUserDetails;
import com.birgundegelecek.proje.dto.SiparisResponse;
import com.birgundegelecek.proje.service.SiparisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/siparis")
@Slf4j

public class SiparisController {

 	
	private final SiparisService siparisService;

    
	
	@PostMapping("/olustur")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<SiparisResponse> siparisOlustur(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getId();
		
		SiparisResponse cevap = siparisService.siparisAl(userId);
		return ResponseEntity.ok(cevap);
	}

}
