package com.birgundegelecek.proje;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.birgundegelecek.proje.repository.SorunRepository;
import com.birgundegelecek.proje.service.SorunService;

@ExtendWith(MockitoExtension.class)
class SorunServiceTest {
	
	@InjectMocks
	private SorunService sorunService;
	
	@Mock
	private SorunRepository sorunRepository;
	
	@Mock
	private KategoriRepository kategoriRepository;
	
	@Test
	void sorunEkle_ExceptionAtacak_EgerIDbulunamazsa() {
		SorunDTO dto = new SorunDTO();
		dto.setBaslik("ad");
		dto.setCozum("asd");
		dto.setKategoriId(1L);
		dto.setSorun("agf");
		
		when(kategoriRepository.findById(1L)).thenReturn(Optional.empty());
		
		KategoriBulunamadıException ex = assertThrows(KategoriBulunamadıException.class, () -> { sorunService.sorunEkle(dto); });
		
		assertEquals("Kategori Bulunamadı", ex.getMessage());
	}
	
	@Test
	void sorunEkle_sorunKaydetmeli_egerIDbulunursa() {
		SorunDTO dto = new SorunDTO();
		dto.setBaslik("ad");
		dto.setCozum("asd");
		dto.setKategoriId(2L);
		dto.setSorun("agf");
		
		Kategori kategori = new Kategori();
		kategori.setAd("test");
		kategori.setId(2L);
		
		Sorun sorun = new Sorun();
		sorun.setBaslik(dto.getBaslik());
		sorun.setCozum(dto.getCozum());
		sorun.setId(1L);
		sorun.setKategori(kategori);
		sorun.setSorun(dto.getSorun());
		
		when(kategoriRepository.findById(2L)).thenReturn(Optional.of(kategori));
		when(sorunRepository.save(any(Sorun.class))).thenReturn(sorun);
		
		SorunDTO result = sorunService.sorunEkle(dto);
		
		assertNotNull(result);
		assertEquals("ad", result.getBaslik());
		assertEquals("asd", result.getCozum());
		assertEquals(2L, result.getKategoriId());
		assertEquals("agf", result.getSorun());
		
		verify(kategoriRepository).findById(2L);
		verify(sorunRepository).save(any(Sorun.class));
		
	}
	
	@Test
	void sorunlarGoster_HataFırlatmalı_EgerExistDegilse() {
		Long kategoriId = 1L;
		
		when(kategoriRepository.existsById(kategoriId)).thenReturn(false);
		
		KategoriBulunamadıException ex = assertThrows(KategoriBulunamadıException.class , () -> sorunService.sorunlarGoster(kategoriId, 1, 2));
		
		assertEquals("Kategori bulunamadı", ex.getMessage());
	}

	

}
