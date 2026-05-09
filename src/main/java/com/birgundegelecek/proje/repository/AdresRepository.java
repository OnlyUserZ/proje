package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Adres;
import com.birgundegelecek.proje.entity.User;

@Repository
public interface AdresRepository extends JpaRepository<Adres, Long> {
	
	boolean existsByAdresSahibi(User adresSahibi);
	
	Optional<Adres> findByAdresSahibi(User adresSahibi);

}
