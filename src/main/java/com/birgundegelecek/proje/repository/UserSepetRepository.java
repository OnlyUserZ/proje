package com.birgundegelecek.proje.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.birgundegelecek.proje.entity.Kategori;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.entity.UserSepet;
import java.util.List;


@Repository
public interface UserSepetRepository extends JpaRepository<UserSepet, Long> {
	
	Optional<UserSepet> findByIdAndDeletedFalse(Long id);
	Optional<UserSepet> findByUser(User user);
	
	@Query("""
		    SELECT DISTINCT us
		    FROM UserSepet us
		    LEFT JOIN FETCH us.sepetUruns su
		    LEFT JOIN FETCH su.urun
		    WHERE us.id = :userSepetId
		""")
		Optional<UserSepet> findByIdWithSepetUrunsAndUrun(Long userSepetId);
	

}
