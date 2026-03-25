package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SepetUrunRequest {
	
	@NotNull
	private long userSepetId;
	
	@NotNull
	private long urunId;
	
	

}
