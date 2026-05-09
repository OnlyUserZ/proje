package com.birgundegelecek.proje.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AdresResponse {
	
	private Long adresId;
	
	private String il;
	
	private String ilce;
	
	private String mahalle;
	
	private String caddeSokak;
	
	private int binaNo;
	
	private int katNo;
	
	private int daireNo;
	
	private String adresTarifi;
	
	private String adresBasligi;

}
