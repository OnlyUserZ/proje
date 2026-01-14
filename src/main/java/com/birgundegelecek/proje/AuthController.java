package com.birgundegelecek.proje;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.exception.RateLimitException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.service.RedisOperations;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisOperations redisOperations;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Login isteği geldi. username={}", request.getUsername());
        
        boolean limit = redisOperations.allowLogin(request);
        if(limit == false) {
             throw new RateLimitException("Çok fazla hatalı deneme yaptınız");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String refreshTokenId = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, refreshTokenId);

        log.info("Login başarılı. username={}", request.getUsername());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("Refresh token isteği geldi.");

        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.warn("Refresh token süresi dolmuş.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String jti = jwtUtil.extractTokenId(refreshToken);
        if (tokenBlacklistService.isBlacklisted(jti)) {
            log.warn("Refresh token kara listede.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newRefreshTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, newRefreshTokenId);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);

        log.info("Refresh token başarılı. username={}", username);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        log.info("Kayıt isteği geldi. username={}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Kullanıcı adı zaten alınmış: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bu kullanıcı adı zaten alınmış.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("USER");

        userRepository.save(newUser);
        log.info("Kayıt başarılı. username={}", request.getUsername());

        return ResponseEntity.ok("Kayıt başarıyla tamamlandı.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequest request) {
        log.info("Logout isteği geldi.");

        String refreshToken = request.getRefreshToken();
        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.warn("Logout sırasında token zaten geçersiz.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token zaten geçersiz.");
        }

        String jti = jwtUtil.extractTokenId(refreshToken);
        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);

        log.info("Logout başarılı. tokenId={}", jti);
        return ResponseEntity.ok("Başarıyla çıkış yapıldı.");
    }
    
    @PutMapping("/resetpassword")
    public ResponseEntity<String> resetPasswordOnline(@RequestBody ResetPasswordRequest request) {
    	    	    
    	    String token = request.getToken();
    	    String newPassword = request.getNewPassword();
 	    
    	    if (jwtUtil.isTokenExpired(token)) {
    	        log.warn("Reset token süresi dolmuş.");
    	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token süresi dolmuş.");
    	    }
   	    
    	    String jti = jwtUtil.extractTokenId(token);
    	    if (tokenBlacklistService.isBlacklisted(jti)) {
    	        log.warn("Reset token kara listede.");
    	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token geçersiz.");
    	    }

    	    String username = jwtUtil.extractUsername(token);
    	    User user = userRepository.findByUsername(username)
    	            .orElseThrow(() -> new UserBulunamadıException("User bulunamadı"));

    	    user.setPassword(passwordEncoder.encode(newPassword));
    	    userRepository.save(user);

    	    tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24); 

    	    log.info("Şifre sıfırlama başarılı. username={}", username);
    	    return ResponseEntity.ok("Şifre başarıyla sıfırlandı.");
    	}
    	
    }

