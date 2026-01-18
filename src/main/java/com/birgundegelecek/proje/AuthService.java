package com.birgundegelecek.proje; 

import com.birgundegelecek.proje.entity.User; 
import com.birgundegelecek.proje.exception.RateLimitException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.UserRepository; 
import com.birgundegelecek.proje.service.RedisOperations; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 
import org.springframework.security.authentication.AuthenticationManager; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisOperations redisOperations;

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt. username={}", request.getUsername());

        try {
            boolean allowed = redisOperations.allowLogin(request);
            if (!allowed) throw new RateLimitException("Çok fazla hatalı deneme yaptınız");

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String refreshTokenId = UUID.randomUUID().toString();

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails, refreshTokenId);

            log.info("Authentication successful. username={}", request.getUsername());

            return new AuthResponse(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("Login failed. username={}, reason={}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı");
        }
    }



    @Transactional
    public AuthResponse refresh(String refreshToken) {
        log.info("REFRESH token request received");

        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.warn("Refresh token expired");
            throw new RuntimeException("Refresh token süresi dolmuş");
        }

        String jti = jwtUtil.extractTokenId(refreshToken);
        log.debug("Refresh token jti extracted: {}", jti);

        if (tokenBlacklistService.isBlacklisted(jti)) {
            log.warn("Refresh token is blacklisted. jti={}", jti);
            throw new RuntimeException("Token geçersiz");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        log.info("Refresh token belongs to username={}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newRefreshTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, newRefreshTokenId);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);
        log.info("Old refresh token blacklisted. oldJti={}", jti);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void register(RegisterRequest request) {
        log.info("REGISTER attempt started. username={}", request.getUsername());

        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            log.warn("Register failed. Username already exists: {}", request.getUsername());
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış.");
        });

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("USER");

        userRepository.save(newUser);

        log.info("User successfully registered. username={}, role={}",
                newUser.getUsername(),
                newUser.getRole()
        );
    }

    public void logout(String refreshToken) {
        log.info("LOGOUT request received");

        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.warn("Logout failed. Token already expired");
            throw new RuntimeException("Token zaten geçersiz.");
        }

        String jti = jwtUtil.extractTokenId(refreshToken);
        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);

        log.info("Logout successful. refreshToken blacklisted. jti={}", jti);
    }

    @Transactional
    public void changePassword(ResetPasswordRequest request) {
        log.info("CHANGE PASSWORD request received");

        String token = request.getToken();

        if (jwtUtil.isTokenExpired(token)) {
            log.warn("Change password failed. Token expired");
            throw new RuntimeException("Token süresi dolmuş");
        }

        String jti = jwtUtil.extractTokenId(token);
        if (tokenBlacklistService.isBlacklisted(jti)) {
            log.warn("Change password failed. Token blacklisted. jti={}", jti);
            throw new RuntimeException("Token geçersiz");
        }

        String username = jwtUtil.extractUsername(token);
        log.info("Change password for username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found while changing password. username={}", username);
                    return new UserBulunamadıException("User bulunamadı");
                });

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Old password mismatch. username={}", username);
            throw new RuntimeException("Mevcut şifre yanlış");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24);

        log.info("Password successfully changed. username={}, token invalidated", username);
    }
}
