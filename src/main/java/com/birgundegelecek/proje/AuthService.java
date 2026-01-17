package com.birgundegelecek.proje;

import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.exception.RateLimitException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.service.RedisOperations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisOperations redisOperations;

    public AuthResponse login(AuthRequest request) {
        boolean allowed = redisOperations.allowLogin(request);
        if (!allowed) throw new RateLimitException("Çok fazla hatalı deneme yaptınız");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String refreshTokenId = UUID.randomUUID().toString();

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, refreshTokenId);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        if (jwtUtil.isTokenExpired(refreshToken)) throw new RuntimeException("Refresh token süresi dolmuş");

        String jti = jwtUtil.extractTokenId(refreshToken);
        if (tokenBlacklistService.isBlacklisted(jti)) throw new RuntimeException("Token geçersiz");

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newRefreshTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, newRefreshTokenId);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("USER");

        userRepository.save(newUser);
    }

    public void logout(String refreshToken) {
        if (jwtUtil.isTokenExpired(refreshToken)) throw new RuntimeException("Token zaten geçersiz.");

        String jti = jwtUtil.extractTokenId(refreshToken);
        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24 * 7);
    }

    @Transactional
    public void changePassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (jwtUtil.isTokenExpired(token)) throw new RuntimeException("Token süresi dolmuş");

        String jti = jwtUtil.extractTokenId(token);
        if (tokenBlacklistService.isBlacklisted(jti)) throw new RuntimeException("Token geçersiz");

        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserBulunamadıException("User bulunamadı"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifre yanlış");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenBlacklistService.blacklistToken(jti, 1000L * 60 * 60 * 24);
    }
}
