package com.birgundegelecek.proje.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.birgundegelecek.proje.entity.Kategori;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrunResponse {
	
	private long id;
	
	private String ad;
	
	private BigDecimal fiyat;
	
	private int stok;
	
	private String aciklama;
	
	Set<Kategori> kategoriler;
	
}
