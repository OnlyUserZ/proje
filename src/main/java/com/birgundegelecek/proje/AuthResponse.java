package com.birgundegelecek.proje;

import com.birgundegelecek.proje.status.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class AuthResponse {
    private String accessToken;  
    private String refreshToken; 
    
}
