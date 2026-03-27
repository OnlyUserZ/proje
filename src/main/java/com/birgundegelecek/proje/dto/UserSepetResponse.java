package com.birgundegelecek.proje.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.birgundegelecek.proje.entity.SepetUrun;
import com.birgundegelecek.proje.entity.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSepetResponse {
	
	@NotNull
	private long id;
	
	@NotNull
	private User user;

	@NotNull
	@PositiveOrZero
	private BigDecimal toplamFiyat;
	
	
	private Set<SepetUrun> sepetUruns;
}
