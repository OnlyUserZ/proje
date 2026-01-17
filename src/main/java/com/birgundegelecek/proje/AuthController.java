package com.birgundegelecek.proje;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        log.info("Login başarılı. username={}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        log.info("Refresh token başarılı.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        log.info("Kayıt başarılı. username={}", request.getUsername());
        return ResponseEntity.ok("Kayıt başarıyla tamamlandı.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        log.info("Logout başarılı.");
        return ResponseEntity.ok("Başarıyla çıkış yapıldı.");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ResetPasswordRequest request) {
        authService.changePassword(request);
        log.info("Şifre başarıyla değiştirildi.");
        return ResponseEntity.ok("Şifre başarıyla değiştirildi.");
    }
}
