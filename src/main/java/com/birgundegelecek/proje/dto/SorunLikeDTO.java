package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SorunLikeDTO {
	
	@NotNull
	private Long user_id;
	
	@NotNull
	private Long sorun_id;

}
