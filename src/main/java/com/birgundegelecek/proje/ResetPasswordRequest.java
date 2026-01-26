package com.birgundegelecek.proje;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private String token;
	
	@NotBlank(message = "Eski şifre boş olamaz")
	private String oldPassword;
	
	@NotBlank(message = "Yeni şifre boş olamaz")
    private String newPassword;

}
