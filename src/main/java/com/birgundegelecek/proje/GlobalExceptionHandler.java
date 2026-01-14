package com.birgundegelecek.proje;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.exception.RateLimitException;
import com.birgundegelecek.proje.exception.SorunBulunamadıException;
import com.birgundegelecek.proje.exception.SorunLikeBulunamadıException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token süresi dolmuş.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwt(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token geçersiz.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı adı veya parola hatalı.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bir hata oluştu: " + ex.getMessage());
    }
    
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<AuthResponse> handleRateLimit(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new AuthResponse(null, null));
    }
    
    @ExceptionHandler({UserBulunamadıException.class,
        KategoriBulunamadıException.class,
        SorunBulunamadıException.class,
        SorunLikeBulunamadıException.class})
        public ResponseEntity<String> handleNotFound(RuntimeException ex) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
               .body(ex.getMessage());
}
    
}
