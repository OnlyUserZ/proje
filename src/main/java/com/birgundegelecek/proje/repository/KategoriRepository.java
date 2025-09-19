package com.birgundegelecek.proje.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
	Page<Kategori> findAll(Pageable pageable);
	

}
