package com.birgundegelecek.proje.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.SiparisResponse;
import com.birgundegelecek.proje.entity.AddressSnapshot;
import com.birgundegelecek.proje.entity.Adres;
import com.birgundegelecek.proje.entity.SepetUrun;
import com.birgundegelecek.proje.entity.Siparis;
import com.birgundegelecek.proje.entity.SiparisUrun;
import com.birgundegelecek.proje.entity.Urun;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.entity.UserSepet;
import com.birgundegelecek.proje.exception.AdresBulunamadıException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.exception.UserSepetBulunamadiException;
import com.birgundegelecek.proje.exception.YetersizMiktarException;
import com.birgundegelecek.proje.repository.AdresRepository;
import com.birgundegelecek.proje.repository.SiparisRepository;
import com.birgundegelecek.proje.repository.UrunRepository;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.repository.UserSepetRepository;
import com.birgundegelecek.proje.status.SiparisStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class SiparisService {
	
	private final UserSepetRepository userSepetRepository;
	private final UserRepository userRepository;
	private final SiparisRepository siparisRepository;
	private final AdresRepository adresRepository;
	private final UrunRepository urunRepository;
	
	@Transactional
    public SiparisResponse siparisAl(Long userId) {

        log.info("Sipariş oluşturma işlemi başladı. userId={}", userId);

        User user = userRepository.findByIdWithUserSepetAndSepetUruns(userId)
                .orElseThrow(() -> {
                    log.error("User bulunamadı. userId={}", userId);
                    return new UserBulunamadıException("User Bulunamadı");
                });

        log.info("User bulundu. userId={}, username={}", user.getId(), user.getUsername());

        UserSepet userSepet = user.getUserSepet();

        Adres adres = adresRepository.findByAdresSahibi(user)
                .orElseThrow(() -> {
                    log.error("Adres bulunamadı. userId={}", userId);
                    return new AdresBulunamadıException("Adres Bulunamadı");
                });

        log.info("Adres bulundu. userId={}, il={}, ilce={}",
                userId,
                adres.getIl(),
                adres.getIlce());

        if (userSepet == null) {
            log.error("UserSepet bulunamadı. userId={}", userId);
            throw new UserSepetBulunamadiException("UserSepet Bulunamadı");
        }

        Set<SepetUrun> sepetUruns = userSepet.getSepetUruns();

        log.info("Sepetteki ürün sayısı: {} userId={}", sepetUruns.size(), userId);

        Siparis siparis = new Siparis();

        List<SiparisUrun> siparisUruns = new ArrayList<>();

        BigDecimal toplamFiyat = BigDecimal.ZERO;

        for (SepetUrun sepetUrun : sepetUruns) {

            Urun urun = urunRepository.findForUpdate(sepetUrun.getUrun().getId());

            int miktar = sepetUrun.getAdet();

            int aktifStok = urun.getStok() - urun.getRezerveStok();

            log.info(
                    "Ürün kontrol ediliyor. urunId={}, aktifStok={}, istenenMiktar={}",
                    urun.getId(),
                    aktifStok,
                    miktar
            );

            if (aktifStok < miktar) {
                throw new YetersizMiktarException("Yetersiz stok: " + urun.getAd());
            }

            urun.setRezerveStok(urun.getRezerveStok() + miktar);

            log.info(
                    "Rezerve stok artırıldı. urunId={}, yeniRezerveStok={}",
                    urun.getId(),
                    urun.getRezerveStok()
            );

            SiparisUrun siparisUrun = new SiparisUrun();

            siparisUrun.setAdet(miktar);
            siparisUrun.setToplam_fiyat(sepetUrun.getToplamFiyat());
            siparisUrun.setUrun(urun);
            siparisUrun.setSiparis(siparis);

            toplamFiyat = toplamFiyat.add(sepetUrun.getToplamFiyat());

            siparisUruns.add(siparisUrun);
        }

        siparis.setCreated_at(LocalDateTime.now());
        siparis.setSahip(user);
        siparis.setSiparisurun(siparisUruns);
        siparis.setToplamFiyat(toplamFiyat);
        siparis.setStatus(SiparisStatus.SIPARIS_ALINDI);

        log.info("Sipariş nesnesi oluşturuldu. userId={}, toplamFiyat={}",
                userId,
                toplamFiyat);

        AddressSnapshot adresSnapshot = new AddressSnapshot();

        adresSnapshot.setAdresBasligi(adres.getAdresBasligi());
        adresSnapshot.setAdresTarifi(adres.getAdresTarifi());
        adresSnapshot.setBinaNo(adres.getBinaNo());
        adresSnapshot.setCaddeSokak(adres.getCaddeSokak());
        adresSnapshot.setDaireNo(adres.getDaireNo());
        adresSnapshot.setIl(adres.getIl());
        adresSnapshot.setIlce(adres.getIlce());
        adresSnapshot.setKatNo(adres.getKatNo());
        adresSnapshot.setMahalle(adres.getMahalle());

        siparis.setAdres(adresSnapshot);

        log.info("Adres snapshot oluşturuldu. userId={}", userId);

        String siparisKodu = siparisKoduOlustur();

        siparis.setSiparisKodu(siparisKodu);

        log.info("Sipariş kodu üretildi. siparisKodu={}", siparisKodu);

        Siparis kaydedilen = siparisRepository.save(siparis);

        log.info(
                "Sipariş başarıyla kaydedildi. siparisId={}, siparisKodu={}, toplamFiyat={}",
                kaydedilen.getId(),
                kaydedilen.getSiparisKodu(),
                kaydedilen.getToplamFiyat()
        );

        SiparisResponse cevap = new SiparisResponse(
                siparisKodu,
                kaydedilen.getToplamFiyat(),
                siparisUruns,
                kaydedilen.getCreated_at(),
                adresSnapshot
        );

        log.info("Sipariş response başarıyla oluşturuldu. siparisKodu={}", siparisKodu);

        return cevap;
    }
	
	private String siparisKoduOlustur() {

	    String random = UUID.randomUUID()
	            .toString()
	            .replace("-", "")
	            .substring(0, 6)
	            .toUpperCase();

	    String date = LocalDate.now()
	            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	    return "SPR-" + date + "-" + random;
	}

}
