package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.SiparisUrun;

@Repository
public interface SiparisUrunRepository extends JpaRepository<SiparisUrun, Long> {
	
	Optional<SiparisUrun> findByIdAndDeletedFalse(Long id);

}
