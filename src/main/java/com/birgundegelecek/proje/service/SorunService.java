package com.birgundegelecek.proje.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.birgundegelecek.proje.BirgundegelecekApplication;
import com.birgundegelecek.proje.dto.SorunDTO;
import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.exception.KategoriBulunamadıException;
import com.birgundegelecek.proje.exception.SorunBulunamadıException;
import com.birgundegelecek.proje.repository.KategoriRepository;
import com.birgundegelecek.proje.repository.SorunRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SorunService {

	private final SorunRepository sorunRepository;
	private final KategoriRepository kategoriRepository;

	@Transactional
	public SorunDTO sorunEkle(SorunDTO dto) {
	    Sorun sorun = new Sorun();
	    sorun.setBaslik(dto.getBaslik());
	    sorun.setCozum(dto.getCozum());
	    sorun.setSorun(dto.getSorun());

	    Kategori kategori = kategoriRepository.findById(dto.getKategoriId())
	        .orElseThrow(() -> new KategoriBulunamadıException("Kategori Bulunamadı"));
	    sorun.setKategori(kategori);

	    Sorun kaydedilen = sorunRepository.save(sorun);
	    
	    SorunDTO responseDto = new SorunDTO();
	    responseDto.setBaslik(kaydedilen.getBaslik());
	    responseDto.setCozum(kaydedilen.getCozum());
	    responseDto.setSorun(kaydedilen.getSorun());
	    responseDto.setKategoriId(kaydedilen.getKategori().getId());

	    return responseDto;
	}

	
	public Page<SorunDTO> sorunlarGoster(Long kategoriId, int page, int size) {

	    if (!kategoriRepository.existsById(kategoriId)) {
	        throw new KategoriBulunamadıException("Kategori bulunamadı");
	    }

	    Pageable pageable = PageRequest.of(page, size, Sort.by("baslik").ascending());

	    Page<Sorun> sorunPage = sorunRepository.findByKategoriId(kategoriId, pageable);

	    
	    return sorunPage.map(k -> {
	        SorunDTO dto = new SorunDTO();
	        dto.setBaslik(k.getBaslik());
	        dto.setCozum(k.getCozum());
	        dto.setSorun(k.getSorun());
	        dto.setKategoriId(k.getKategori().getId());
	        return dto; 
	    });
	}

	
	@Transactional
	public void sorunGuncelle(Long id , SorunDTO dto) {
		Sorun sorun = sorunRepository.findById(id).orElseThrow(() -> new SorunBulunamadıException("Sorun Bulunamadı"));
		Kategori kategori = kategoriRepository.findById(dto.getKategoriId()).orElseThrow(() -> new KategoriBulunamadıException("Kategori Bulunamadı"));
		
		sorun.setBaslik(dto.getBaslik());
		sorun.setCozum(dto.getCozum());
		sorun.setKategori(kategori);
		sorun.setSorun(dto.getSorun());
		
		sorunRepository.save(sorun);
	}
	
	@Transactional
	public void topluSil(List<Long> ids) {
	 
	    List<Long> bulunmayan = ids.stream()
	            .filter(id -> !sorunRepository.existsById(id))
	            .toList();

	    if (!bulunmayan.isEmpty()) {
	        throw new SorunBulunamadıException("Bulunamayan Sorun ID'leri: " + bulunmayan);
	    }

	    sorunRepository.deleteAllById(ids);
	}



}
