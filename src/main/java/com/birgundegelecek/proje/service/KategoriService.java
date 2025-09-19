package com.birgundegelecek.proje.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.KategoriDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class KategoriService {
	
	private final KategoriRepository kategoriRepository;
	
	@Transactional
	public KategoriDTO kategoriEkle(KategoriDTO dto) {
		
		Kategori kategori = new Kategori();
		kategori.setAd(dto.getAd());
		
		Kategori kaydedilen = kategoriRepository.save(kategori);
		
		KategoriDTO cevap = new KategoriDTO();
		
		cevap.setAd(kaydedilen.getAd());
		cevap.setId(kaydedilen.getId());
		
		return cevap;
	}
	
	public Page<KategoriDTO> kategorilerGoster(int page, int size) {
		
	    Pageable pageable = PageRequest.of(page, size, Sort.by("ad").ascending());
	    Page<Kategori> kategoriPage = kategoriRepository.findAll(pageable);

	    return kategoriPage.map(k -> {
	        KategoriDTO dto = new KategoriDTO();
	        dto.setAd(k.getAd());
	        dto.setId(k.getId());
	        return dto;
	    });
	}

	
	 @Transactional
	    public void kategorilerSil(List<Long> ids) {
	       
	        List<Long> bulunmayan = ids.stream()
	                .filter(id -> !kategoriRepository.existsById(id))
	                .toList();

	        if (!bulunmayan.isEmpty()) {
	            throw new KategoriBulunamadıException("Bulunamayan Kategori ID'leri: " + bulunmayan);
	            
	        }

	        kategoriRepository.deleteAllById(ids);
	    }
	 
	 @Transactional
	 public void kategoriGuncelle(Long id , KategoriDTO dto) {
		 
		 Kategori kategori = kategoriRepository.findById(id).orElseThrow(() -> new KategoriBulunamadıException("Kategori Bulunamadı"));
		 
		 kategori.setAd(dto.getAd());
		 kategori.setId(dto.getId());
		 
		  kategoriRepository.save(kategori);
	 }
	}