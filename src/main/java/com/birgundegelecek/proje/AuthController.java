package com.birgundegelecek.proje;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
public class AuthController {

    private final AuthenticationManager authenticationManager; 
    private final CustomUserDetailsService userDetailsService; 
    private final JwtUtil jwtUtil; 
    private final TokenBlacklistService tokenBlacklistService; 

    
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        
        String refreshTokenId = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, refreshTokenId);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    
    @PostMapping("/refresh")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String jti = jwtUtil.extractTokenId(refreshToken);

        if (tokenBlacklistService.isBlacklisted(jti)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

       
        String newRefreshTokenId = UUID.randomUUID().toString();

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, newRefreshTokenId);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }

   
    @PostMapping("/logout")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> logout(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token zaten geçersiz.");
        }

        String jti = jwtUtil.extractTokenId(refreshToken);
        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7); // 7 gün blacklist süresi (PROJENE ÖZEL)

        return ResponseEntity.ok("Başarıyla çıkış yapıldı.");
    }
}

