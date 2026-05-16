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
import com.birgundegelecek.proje.repository.AdresRepository;
import com.birgundegelecek.proje.repository.SiparisRepository;
import com.birgundegelecek.proje.repository.UserRepository;
import com.birgundegelecek.proje.repository.UserSepetRepository;
import com.birgundegelecek.proje.status.SiparisStatus;

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
	
	
	public SiparisResponse siparisAl(Long userId) {
		
		User user = userRepository.findByIdWithUserSepetAndSepetUruns(userId).orElseThrow(() -> new UserBulunamadıException("User Bulunamadı"));
		
		UserSepet userSepet = user.getUserSepet();
		
		Adres adres = adresRepository.findByAdresSahibi(user).orElseThrow(() -> new AdresBulunamadıException("Adres Bulunamadı"));
		
		
		if(userSepet == null) {
			throw new UserSepetBulunamadiException("UserSepet Bulunamadı");
		}
		
		Set<SepetUrun> sepetUruns = userSepet.getSepetUruns();
		
		Siparis siparis = new Siparis();
		
		List<SiparisUrun> siparisUruns = new ArrayList<>();
		
		BigDecimal toplamFiyat = BigDecimal.ZERO;
		
		for (SepetUrun sepetUrun : sepetUruns) {
			
			SiparisUrun siparisUrun = new SiparisUrun();
			
		    Urun urun = sepetUrun.getUrun();
		    
		    urun.setAktifStok(urun.getAktifStok() - sepetUrun.getAdet());
		    urun.setRezerveStok(urun.getRezerveStok() + sepetUrun.getAdet());
		    
			siparisUrun.setAdet(sepetUrun.getAdet());
			siparisUrun.setToplam_fiyat(sepetUrun.getToplamFiyat());
			siparisUrun.setUrun(sepetUrun.getUrun());
			
			siparisUrun.setSiparis(siparis);
			
			toplamFiyat = toplamFiyat.add(sepetUrun.getToplamFiyat());
			
			siparisUruns.add(siparisUrun);
		}
		
		siparis.setCreated_at(LocalDateTime.now());
		siparis.setSahip(user);
		siparis.setSiparisurun(siparisUruns);
		siparis.setToplamFiyat(toplamFiyat);
		siparis.setStatus(SiparisStatus.SIPARIS_ALINDI);
		
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
		
		String siparisKodu = siparisKoduOlustur();
		
		siparis.setSiparisKodu(siparisKodu);
		
		Siparis kaydedilen = siparisRepository.save(siparis);
		
		SiparisResponse cevap = new SiparisResponse(siparisKodu, kaydedilen.getToplamFiyat() , siparisUruns, kaydedilen.getCreated_at() , adresSnapshot);
		
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
