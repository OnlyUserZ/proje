package com.birgundegelecek.proje;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.birgundegelecek.proje.dto.KategoriDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.birgundegelecek.proje.service.KategoriService;

@ExtendWith(MockitoExtension.class)
class KategoriServiceTest {
	
	@InjectMocks
	private KategoriService kategoriService;
	
	@Mock
	private KategoriRepository kategoriRepository;
	
	@Test
	void kategoriEkle_kategoriEklenmeli() {
		
		KategoriDTO dto = new KategoriDTO();
		dto.setAd("yarac");
		
		Kategori kategori = new Kategori();
		kategori.setAd(dto.getAd());
		kategori.setId(1L);
		
		when(kategoriRepository.save(any(Kategori.class))).thenReturn(kategori);
		
		KategoriDTO result = kategoriService.kategoriEkle(dto);
		
		assertNotNull(result);
		assertEquals("yarac", result.getAd());
		assertEquals(1L, result.getId());
		
		
	}
	
	@Test
	void kategoriGoster_gostermeli() {
		
		int page = 2;
		int size = 3;
		
		Kategori kategori1 = new Kategori();
		kategori1.setId(1L);
		kategori1.setAd("k1");
		
		Kategori kategori2 = new Kategori();
		kategori2.setId(2L);
		kategori2.setAd("k2");
		
		List<Kategori> kategoriler = List.of(kategori1 , kategori2);
		Page<Kategori> kategoriPage  = new PageImpl<>(kategoriler);
		
		when(kategoriRepository.findAll(any(Pageable.class))).thenReturn(kategoriPage);
		
		Page<KategoriDTO> result = kategoriService.kategorilerGoster(page, size);
		
		assertNotNull(result);
		assertEquals(2, page);
		assertEquals(3, size);
		assertEquals(2, result.getSize());
		assertEquals("k1", result.getContent().get(0).getAd());
	
		
	}
	
	@Test
	void kategorilerSil_tumIdlerMevcut_deleteCagir() {
	    List<Long> ids = List.of(1L, 2L);

	    when(kategoriRepository.existsById(1L)).thenReturn(true);
	    when(kategoriRepository.existsById(2L)).thenReturn(true);

	    assertDoesNotThrow(() -> kategoriService.kategorilerSil(ids));

	    verify(kategoriRepository, times(1)).deleteAllById(ids);
	}

	@Test
	void kategorilerSil_bazıIdlerYok_exceptionFırlat() {
	    List<Long> ids = List.of(1L, 2L);

	    when(kategoriRepository.existsById(1L)).thenReturn(true);
	    when(kategoriRepository.existsById(2L)).thenReturn(false);

	    KategoriBulunamadıException exception = assertThrows(
	        KategoriBulunamadıException.class,
	        () -> kategoriService.kategorilerSil(ids)
	    );

	    assertTrue(exception.getMessage().contains("2"));

	    verify(kategoriRepository, never()).deleteAllById(any());
	}
	
	@Test
	void kategoriGuncelle_KategoriVarsaGuncelle() {
		
		Kategori kategori = new Kategori();
		kategori.setAd("eski");
		kategori.setId(1L);
		
		KategoriDTO dto = new KategoriDTO();
		dto.setAd("yeni");
		dto.setId(1L);
		
		when(kategoriRepository.findById(1L)).thenReturn(Optional.of(kategori));
		when(kategoriRepository.save(any(Kategori.class))).thenReturn(kategori);
		
		assertEquals("yeni", kategori.getAd());
	    assertEquals(1L, kategori.getId());
		
		verify(kategoriRepository).save(any(Kategori.class));
		
		
	}
	
	@Test
	void kategoriGuncelle_KategoriYoksaHataAt() {
		
		when(kategoriRepository.findById(1L)).thenReturn(Optional.empty());
		
		KategoriBulunamadıException exception = assertThrows(KategoriBulunamadıException.class , () -> { 
			kategoriService.kategoriGuncelle(1L, new KategoriDTO());
		});
		
		assertEquals("Kategori Bulunamadı", exception.getMessage());
	}
	
	

}
