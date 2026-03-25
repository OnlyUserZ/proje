package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import java.util.List;


@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {

	boolean existsByAdAndDeletedFalse(String ad);
	
	@Query("SELECT k FROM Kategori k WHERE k.ad = :ad AND k.deleted = false")
	Optional<Kategori> findByAd(@Param("ad") String ad);
	
	@Query("SELECT k FROM Kategori k WHERE k.deleted = false")
	List<Kategori> findAllActiveKategoris();
	
	
	Optional<Kategori> findByIdAndDeletedFalse(Long id);
	
	
	

}