package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.SepetUrun;
import java.util.List;
import com.birgundegelecek.proje.entity.Urun;
import com.birgundegelecek.proje.entity.UserSepet;



@Repository
public interface SepetUrunRepository extends JpaRepository<SepetUrun, Long> {
	
	Optional<SepetUrun> findByIdAndDeletedFalse(Long id);
	Optional<SepetUrun> findByUserSepetAndUrun(UserSepet userSepet, Urun urun);

}
