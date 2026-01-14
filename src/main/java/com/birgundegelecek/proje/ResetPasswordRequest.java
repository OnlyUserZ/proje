package com.birgundegelecek.proje;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private String token;
    private String newPassword;

}
