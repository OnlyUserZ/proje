package com.birgundegelecek.proje.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SepetUrunResponse {
	
	@NotNull
	private Long id;
	
	@NotNull
	@Positive
	private int adet;
	
	private BigDecimal toplamFiyat;
	
	private String urununAdi;

}