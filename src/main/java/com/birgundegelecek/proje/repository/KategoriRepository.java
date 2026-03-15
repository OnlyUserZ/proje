package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import java.util.List;


@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
	
	Page<Kategori> findAll(Pageable pageable);
	boolean existsByAd(String ad);
	Optional<Kategori> findByAd(String ad);
	
	@Query("SELECT k FROM Kategori k WHERE k.deleted = false")
	List<Kategori> findAllActiveKategoris();
	

}
