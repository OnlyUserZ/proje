package com.birgundegelecek.proje.service;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birgundegelecek.proje.dto.SepetUrunRequest;
import com.birgundegelecek.proje.dto.SepetUrunResponse;
import com.birgundegelecek.proje.entity.SepetUrun;
import com.birgundegelecek.proje.entity.Urun;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.entity.UserSepet;
import com.birgundegelecek.proje.exception.UrunBulunamadiException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.SepetUrunRepository;
import com.birgundegelecek.proje.repository.UrunRepository;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.repository.UserSepetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSepetService {
	
	private final SepetUrunRepository sepetUrunRepository;
	private final UserSepetRepository userSepetRepository;
	private final UrunRepository urunRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public SepetUrunResponse sepeteUrunEkle(Long userId, Long urunId) {

	    log.info("Sepete ürün ekleme başlatıldı: userId={}, urunId={}", userId, urunId);

	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> {
	            log.error("User bulunamadı: userId={}", userId);
	            return new UserBulunamadıException("User bulunamadı");
	        });
	    log.info("User bulundu: userId={}", userId);

	    Urun urun = urunRepository.findById(urunId)
	        .orElseThrow(() -> {
	            log.error("Ürün bulunamadı: urunId={}", urunId);
	            return new UrunBulunamadiException("Ürün bulunamadı");
	        });
	    log.info("Ürün bulundu: urunId={}, urunAd={}", urunId, urun.getAd());

	    UserSepet userSepet = userSepetRepository.findByUser(user)
	        .orElseGet(() -> {
	            UserSepet yeniSepet = new UserSepet();
	            yeniSepet.setUser(user);
	            yeniSepet.setToplam_fiyat(BigDecimal.ZERO);
	            UserSepet kaydedilenSepet = userSepetRepository.save(yeniSepet);
	            log.info("Yeni sepet oluşturuldu: userId={}, sepetId={}", userId, kaydedilenSepet.getId());
	            return kaydedilenSepet;
	        });

	    SepetUrun sepetUrun = sepetUrunRepository
	        .findByUserSepetAndUrun(userSepet, urun)
	        .orElse(null);

	    if (sepetUrun != null) {
	        log.info("Sepette ürün mevcut, adet ve fiyat güncelleniyor: sepetUrunId={}", sepetUrun.getId());
	        
	        sepetUrun.setAdet(sepetUrun.getAdet() + 1);

	        BigDecimal yeniToplam = sepetUrun.getToplamFiyat()
	            .add(urun.getFiyat());

	        sepetUrun.setToplamFiyat(yeniToplam);

	        log.info("Sepet ürünü güncellendi: sepetUrunId={}, yeniAdet={}, yeniToplam={}",
	                sepetUrun.getId(), sepetUrun.getAdet(), sepetUrun.getToplamFiyat());

	    } else {
	        log.info("Sepette ürün yok, yeni SepetUrun oluşturuluyor: urunId={}", urunId);
	        
	        sepetUrun = new SepetUrun();
	        sepetUrun.setAdet(1);
	        sepetUrun.setUrun(urun);
	        sepetUrun.setUserSepet(userSepet);
	        sepetUrun.setToplamFiyat(urun.getFiyat());

	        userSepet.getSepetUruns().add(sepetUrun);

	        log.info("Yeni SepetUrun sepete eklendi: urunId={}, sepetId={}", urunId, userSepet.getId());
	    }

	    BigDecimal eskiToplamSepet = userSepet.getToplam_fiyat();
	    userSepet.setToplam_fiyat(
	        userSepet.getToplam_fiyat().add(urun.getFiyat())
	    );
	    log.info("Sepet toplam fiyat güncellendi: sepetId={}, eskiToplam={}, yeniToplam={}",
	            userSepet.getId(), eskiToplamSepet, userSepet.getToplam_fiyat());

	    sepetUrunRepository.save(sepetUrun);
	    userSepetRepository.save(userSepet);
	    log.info("Sepet ve SepetUrun DB'ye kaydedildi: sepetId={}, sepetUrunId={}", userSepet.getId(), sepetUrun.getId());

	    return new SepetUrunResponse(
	        sepetUrun.getId(),
	        sepetUrun.getAdet(),
	        sepetUrun.getToplamFiyat(),
	        urun.getAd()
	    );
	}

}