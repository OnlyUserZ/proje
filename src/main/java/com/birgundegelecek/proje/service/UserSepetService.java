package com.birgundegelecek.proje.service;

import java.math.BigDecimal;
import java.time.Duration;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.birgundegelecek.proje.dto.SepetUrunResponse;
import com.birgundegelecek.proje.dto.UserSepetResponse;
import com.birgundegelecek.proje.entity.SepetUrun;
import com.birgundegelecek.proje.entity.Urun;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.entity.UserSepet;
import com.birgundegelecek.proje.exception.SepetUrunBulunamadıException;
import com.birgundegelecek.proje.exception.UrunBulunamadiException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.SepetUrunRepository;
import com.birgundegelecek.proje.repository.UrunRepository;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.repository.UserSepetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	
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
	
	@Transactional
	public SepetUrunResponse sepettenUrunSil(Long userId, Long sepeturunId) {

	    log.info("Sepetten ürün silme başlatıldı: userId={}, sepetUrunId={}", userId, sepeturunId);

	    SepetUrun sepetUrun = sepetUrunRepository
	            .findByIdAndUserId(sepeturunId, userId)
	            .orElseThrow(() -> {
	                log.error("SepetUrun bulunamadı: userId={}, sepetUrunId={}", userId, sepeturunId);
	                return new SepetUrunBulunamadıException("SepetUrun Bulunamadı");
	            });

	    log.info("SepetUrun bulundu: id={}, mevcutAdet={}", sepetUrun.getId(), sepetUrun.getAdet());

	    if (!sepetUrun.getUserSepet().getUser().getId().equals(userId)) {
	        log.error("Yetkisiz işlem: userId={}, sepetUrunId={}", userId, sepeturunId);
	        throw new AccessDeniedException("Değiştirmeye çalıştığınız sepet sizin değil");
	    }

	    int mevcutAdet = sepetUrun.getAdet();

	    if (mevcutAdet <= 0) {
	        log.error("Hatalı adet durumu: sepetUrunId={}, adet={}", sepeturunId, mevcutAdet);
	        throw new IllegalStateException("Sepet ürün adedi zaten 0 veya hatalı");
	    }

	    if (mevcutAdet == 1) {
	        log.info("Son ürün, siliniyor: sepeturunId={}", sepeturunId);

	        sepetUrunRepository.delete(sepetUrun);
	        
	        redisTemplate.delete("user:" + userId);

	        return new SepetUrunResponse(
	                sepeturunId,
	                0,
	                BigDecimal.ZERO,
	                sepetUrun.getUrun().getAd()
	        );
	    }

	    int yeniAdet = mevcutAdet - 1;
	    sepetUrun.setAdet(yeniAdet);

	    BigDecimal yeniToplamFiyat = sepetUrun.getToplamFiyat()
	            .subtract(sepetUrun.getUrun().getFiyat());

	    sepetUrun.setToplamFiyat(yeniToplamFiyat);

	    log.info("SepetUrun güncellendi: sepetUrunId={}, eskiAdet={}, yeniAdet={}, yeniToplamFiyat={}",
	            sepeturunId, mevcutAdet, yeniAdet, yeniToplamFiyat);

	    return new SepetUrunResponse(
	            sepeturunId,
	            yeniAdet,
	            yeniToplamFiyat,
	            sepetUrun.getUrun().getAd()
	    );
	}
	
	@Transactional(readOnly = true)
	public UserSepetResponse sepetiGoster(Long userId) {

	    String key = "sepet:" + userId;

	    log.info("Sepet gösterme başlatıldı: userId={}", userId);

	    try {
	        String cached = redisTemplate.opsForValue().get(key);

	        if (cached != null && !cached.isEmpty()) {
	            log.info("Cache hit: userId={}", userId);
	            return objectMapper.readValue(cached, UserSepetResponse.class);
	        }

	        log.info("Cache miss: userId={}", userId);

	    } catch (Exception e) {
	        log.error("Redis read error: userId={}", userId, e);
	    }

	    User user = userRepository.findByIdWithUserSepet(userId)
	            .orElseThrow(() -> {
	                log.error("User bulunamadı: userId={}", userId);
	                return new UserBulunamadıException("User Bulunamadı");
	            });

	    log.info("User bulundu: userId={}", userId);

	    UserSepet userSepet = user.getUserSepet();

	    if (!userSepet.getUser().getId().equals(userId)) {
	        log.error("Yetkisiz sepet erişimi: userId={}", userId);
	        throw new AccessDeniedException("Değiştirmeye çalıştığınız sepet sizin değil");
	    }

	    UserSepetResponse response = new UserSepetResponse(
	            userSepet.getId(),
	            user,
	            userSepet.getToplam_fiyat(),
	            userSepet.getSepetUruns()
	    );

	    try {
	        redisTemplate.opsForValue().set(
	                key,
	                objectMapper.writeValueAsString(response),
	                Duration.ofMinutes(5)
	        );

	        log.info("Cache write başarılı: userId={}", userId);

	    } catch (Exception e) {
	        log.error("Redis write error: userId={}", userId, e);
	    }

	    return response;
	}

}