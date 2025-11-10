package com.birgundegelecek.proje.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.SorunLike;
import com.birgundegelecek.proje.entity.Sorun;
import com.birgundegelecek.proje.entity.User;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;



@Repository
public interface SorunLikeRepository extends JpaRepository<SorunLike, Long> {
	
   boolean existsBySorunAndUser(Sorun sorun, User user);
   void deleteBySorunAndUser(Sorun sorun, User user);
   Optional<SorunLike> findBySorunAndUser(Sorun sorun, User user);
   long countBySorun(Sorun sorun);
}
