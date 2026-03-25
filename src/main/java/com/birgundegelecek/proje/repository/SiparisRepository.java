package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.Siparis;

@Repository
public interface SiparisRepository extends JpaRepository<Siparis, Long>{
	
	Optional<Siparis> findByIdAndDeletedFalse(Long id);
	

}
