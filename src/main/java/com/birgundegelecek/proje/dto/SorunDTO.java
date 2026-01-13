package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SorunDTO {
	
	@NotBlank(message = "Baslik Bos Olamaz")
	private String baslik;
	
	@NotBlank(message = "Sorun Bos Olamaz")
	@Size(min = 1 , max = 600 , message = "Sorun Adı 1 ila 600 karakter arasında olmalı")
	private String sorun;
	
	@NotBlank(message = "Cozum Bos Olamaz")
	@Size(min = 1 , max = 600 , message = "Cozum adi 1 ila 600 karakter arasında olmalı")
	private String cozum;
	
	@NotNull
	private Long kategoriId;

}
