package com.birgundegelecek.proje.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Siparis;

@Repository
public interface SiparisRepository extends JpaRepository<Siparis, Long>{
	

}
