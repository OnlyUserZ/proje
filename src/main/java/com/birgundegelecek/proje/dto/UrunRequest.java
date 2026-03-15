package com.birgundegelecek.proje.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrunRequest {
	
	@NotBlank
	@Size(min = 1 , max = 60 , message = "Ürün adı 1 ila 60 karakter arasında olmalı")
	private String ad;
	
	@NotBlank
	@Size(min = 1 , max = 500)
	private String aciklama;
	
	@Positive(message = "Ürün fiyatı sıfırdan büyük olmalıdır")
	private BigDecimal fiyat;
	
	@PositiveOrZero(message = "Stok miktarı eksi değer olamaz")
	private int stok;
	
	private List<Long> kategori_ids;

}
