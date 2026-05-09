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
@AllArgsConstructor
@NoArgsConstructor

public class AdresRequest {
	
	@NotBlank
    @Size(max = 50)
    private String il;

    @NotBlank
    @Size(max = 50)
    private String ilce;

    @NotBlank
    @Size(max = 50)
    private String mahalle;

    @NotBlank
    @Size(max = 100)
    private String caddeSokak;

    @NotBlank
    @Size(max = 10)
    private int binaNo;

    @NotBlank
    @Size(max = 5)
    private int katNo;

    @NotBlank
    @Size(max = 5)
    private int daireNo;

    @NotBlank
    @Size(max = 255)
    private String adresTarifi;

    @NotBlank
    @Size(max = 50)
    private String adresBasligi;
    
    @NotNull
    private Long adresSahibiId;
}
