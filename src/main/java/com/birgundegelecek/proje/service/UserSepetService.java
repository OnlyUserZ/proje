package com.birgundegelecek.proje.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.birgundegelecek.proje.exception.UserSepetBulunamadiException;
import com.birgundegelecek.proje.exception.YetersizMiktarException;
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

        User user = userRepository.findByIdWithUserSepet(userId)
                .orElseThrow(() -> new UserBulunamadıException("User bulunamadı"));

        Urun urun = urunRepository.findById(urunId)
                .orElseThrow(() -> new UrunBulunamadiException("Ürün bulunamadı"));

        UserSepet userSepet = user.getUserSepet();
        if (userSepet == null) {
            userSepet = new UserSepet();
            userSepet.setUser(user);
            userSepet.setToplam_fiyat(BigDecimal.ZERO);
            userSepet = userSepetRepository.save(userSepet);
            user.setUserSepet(userSepet);
            log.info("Yeni sepet oluşturuldu: userId={}, sepetId={}", userId, userSepet.getId());
        }

        if (!userSepet.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bu sepete erişim yetkiniz yok");
        }

        SepetUrun sepetUrun = sepetUrunRepository
                .findByUserSepetAndUrun(userSepet, urun)
                .orElse(null);

        if (sepetUrun != null) {
            sepetUrun.setAdet(sepetUrun.getAdet() + 1);
            sepetUrun.setToplamFiyat(sepetUrun.getToplamFiyat().add(urun.getFiyat()));
            log.info("Sepet ürünü güncellendi: sepetUrunId={}, yeniAdet={}, yeniToplamFiyat={}",
                    sepetUrun.getId(), sepetUrun.getAdet(), sepetUrun.getToplamFiyat());
        } else {
            sepetUrun = new SepetUrun();
            sepetUrun.setAdet(1);
            sepetUrun.setUrun(urun);
            sepetUrun.setUserSepet(userSepet);
            sepetUrun.setToplamFiyat(urun.getFiyat());
            userSepet.getSepetUruns().add(sepetUrun);
            log.info("Yeni SepetUrun sepete eklendi: urunId={}, sepetId={}", urunId, userSepet.getId());
        }

        userSepet.setToplam_fiyat(userSepet.getToplam_fiyat().add(urun.getFiyat()));

        sepetUrunRepository.save(sepetUrun);
        userSepetRepository.save(userSepet);

        log.info("Sepet ve SepetUrun kaydedildi: sepetId={}, sepetUrunId={}", userSepet.getId(), sepetUrun.getId());

        return new SepetUrunResponse(sepetUrun.getId(), sepetUrun.getAdet(), sepetUrun.getToplamFiyat(), urun.getAd());
    }

    @Transactional
    public SepetUrunResponse sepettenUrunSil(Long userId, Long sepetUrunId) {
        log.info("Sepetten ürün silme başlatıldı: userId={}, sepetUrunId={}", userId, sepetUrunId);

        SepetUrun sepetUrun = sepetUrunRepository.findById(sepetUrunId)
                .orElseThrow(() -> new SepetUrunBulunamadıException("SepetUrun Bulunamadı"));

        UserSepet userSepet = sepetUrun.getUserSepet();

        if (!userSepet.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bu sepete erişim yetkiniz yok");
        }

        int mevcutAdet = sepetUrun.getAdet();

        if (mevcutAdet <= 0) {
            throw new IllegalStateException("Sepet ürün adedi zaten 0 veya hatalı");
        }

        if (mevcutAdet == 1) {
            sepetUrunRepository.delete(sepetUrun);
            userSepet.setToplam_fiyat(userSepet.getToplam_fiyat().subtract(sepetUrun.getToplamFiyat()));
            userSepetRepository.save(userSepet);

            return new SepetUrunResponse(sepetUrunId, 0, BigDecimal.ZERO, sepetUrun.getUrun().getAd());
        }

        sepetUrun.setAdet(mevcutAdet - 1);
        sepetUrun.setToplamFiyat(sepetUrun.getToplamFiyat().subtract(sepetUrun.getUrun().getFiyat()));

        userSepet.setToplam_fiyat(userSepet.getToplam_fiyat().subtract(sepetUrun.getUrun().getFiyat()));
        sepetUrunRepository.save(sepetUrun);
        userSepetRepository.save(userSepet);

        return new SepetUrunResponse(sepetUrunId, sepetUrun.getAdet(), sepetUrun.getToplamFiyat(), sepetUrun.getUrun().getAd());
    }

    @Transactional(readOnly = true)
    public UserSepetResponse sepetiGoster(Long userId) {
        User user = userRepository.findByIdWithUserSepet(userId)
                .orElseThrow(() -> new UserBulunamadıException("User Bulunamadı"));

        UserSepet userSepet = user.getUserSepet();

        if (!userSepet.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bu sepete erişim yetkiniz yok");
        }

        return new UserSepetResponse(userSepet.getId(), user, userSepet.getToplam_fiyat(), userSepet.getSepetUruns());
    }
    
    @Transactional
    public UserSepetResponse sepetiKontrolEt(Long userId) {

        User user = userRepository.findByIdWithUserSepet(userId)
                .orElseThrow(() ->
                        new UserBulunamadıException("User bulunamadı"));

        UserSepet userSepet = user.getUserSepet();

        if (userSepet == null) {
            throw new UserSepetBulunamadiException("Sepet bulunamadı");
        }

        userSepet = userSepetRepository
                .findByIdWithSepetUrunsAndUrun(userSepet.getId())
                .orElseThrow(() ->
                        new UserSepetBulunamadiException("Sepet bulunamadı"));

        List<SepetUrun> silinecekler = new ArrayList<>();

        BigDecimal yeniToplamFiyat = BigDecimal.ZERO;

        for (SepetUrun sepetUrun : userSepet.getSepetUruns()) {

            Urun urun = sepetUrun.getUrun();

            int stok = urun.getStok();
            int mevcutAdet = sepetUrun.getAdet();

            if (stok <= 0) {

                silinecekler.add(sepetUrun);

                continue;
            }

            if (stok < mevcutAdet) {

                sepetUrun.setAdet(stok);

                sepetUrun.setToplamFiyat(
                        urun.getFiyat().multiply(BigDecimal.valueOf(stok))
                );
            }

            yeniToplamFiyat =
                    yeniToplamFiyat.add(sepetUrun.getToplamFiyat());
        }

        for (SepetUrun s : silinecekler) {
            userSepet.getSepetUruns().remove(s);
            sepetUrunRepository.delete(s);
        }

        userSepet.setToplam_fiyat(yeniToplamFiyat);

        return new UserSepetResponse(
                userSepet.getId(),
                userSepet.getUser(),
                userSepet.getToplam_fiyat(),
                userSepet.getSepetUruns()
        );
    }
    	
}