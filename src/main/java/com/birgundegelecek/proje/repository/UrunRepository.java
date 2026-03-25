package com.birgundegelecek.proje.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Urun;

@Repository
public interface UrunRepository extends JpaRepository<Urun, Long> {

	@Query("SELECT u FROM Urun u JOIN u.kategoriler k WHERE k.id = :id")
	Page<Urun> findByKategori_id(@Param("id") Long id , Pageable pageable);
	
	Optional<Urun> findById(long id);
}