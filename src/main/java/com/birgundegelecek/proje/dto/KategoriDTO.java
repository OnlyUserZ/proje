package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KategoriDTO {
	
	private Long id;
	
	
	@NotBlank(message = "Kategori Adı Bos Olamaz")
	private String ad;

}
