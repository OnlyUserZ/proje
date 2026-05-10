package com.birgundegelecek.proje.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.birgundegelecek.proje.entity.AddressSnapshot;
import com.birgundegelecek.proje.entity.Adres;
import com.birgundegelecek.proje.entity.SiparisUrun;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiparisResponse {
	
	@NotBlank
	private String siparisKodu;
	
	@PositiveOrZero
	@NotNull
	private BigDecimal toplamFiyat;
	
	@NotNull
	private List<SiparisUrun> siparisUruns;
	
	@NotNull
	private LocalDateTime created_at;
	
	@NotNull
	private AddressSnapshot adres;

}
