package com.birgundegelecek.proje.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birgundegelecek.proje.dto.UserDTO;
import com.birgundegelecek.proje.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO dto) {
        userService.register(dto);
        return ResponseEntity.ok("Kayıt başarılı");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO dto) {
        boolean success = userService.login(dto);
        if (success == true) {
            return ResponseEntity.ok("Giriş başarılı");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı adı veya şifre yanlış");
        }
    }
}
