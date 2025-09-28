package com.birgundegelecek.proje.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.SorunLike;

@Repository
public interface SorunLikeRepository extends JpaRepository<SorunLike, Long> {
	

}
