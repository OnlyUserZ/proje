package com.birgundegelecek.proje.controller;

import com.birgundegelecek.proje.dto.SepetUrunResponse;
import com.birgundegelecek.proje.dto.UserSepetResponse;
import com.birgundegelecek.proje.exception.UnauthorizedException;
import com.birgundegelecek.proje.CustomUserDetails;
import com.birgundegelecek.proje.service.UserSepetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/sepet")
@RequiredArgsConstructor
public class UserSepetController {

    private final UserSepetService userSepetService;

    @PostMapping("/items")
    public ResponseEntity<SepetUrunResponse> sepeteUrunEkle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long urunId
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Long userId = userDetails.getId();

        SepetUrunResponse response =
                userSepetService.sepeteUrunEkle(userId, urunId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/kontrol")
    public ResponseEntity<UserSepetResponse> sepetiKontrolEt(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        if (userDetails == null) {
            throw new UnauthorizedException("User yetkili değil");
        }

        Long userId = userDetails.getId();

        UserSepetResponse response =
                userSepetService.sepetiKontrolEt(userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{sepetUrunId}")
    public ResponseEntity<Void> sepettenUrunSil(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long sepetUrunId
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Long userId = userDetails.getId();

        userSepetService.sepettenUrunSil(userId, sepetUrunId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserSepetResponse> sepetiGoster(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        Long userId = userDetails.getId();

        UserSepetResponse response =
                userSepetService.sepetiGoster(userId);

        return ResponseEntity.ok(response);
    }
}