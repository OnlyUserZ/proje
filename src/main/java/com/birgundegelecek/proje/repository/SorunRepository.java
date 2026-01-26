package com.birgundegelecek.proje.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Sorun;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SorunRepository extends JpaRepository<Sorun, Long> {
	
    Page<Sorun> findByKategoriId(Long id , Pageable pageable);

}
