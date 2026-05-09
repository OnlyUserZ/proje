package com.birgundegelecek.proje.service;

import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.AdresRequest;
import com.birgundegelecek.proje.dto.AdresResponse;
import com.birgundegelecek.proje.entity.Adres;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.exception.AdresZatenMevcutException;
import com.birgundegelecek.proje.exception.UserBulunamadıException;
import com.birgundegelecek.proje.repository.AdresRepository;
import com.birgundegelecek.proje.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdresService {

    private final UserRepository userRepository;
    private final AdresRepository adresRepository;

    @Transactional
    public AdresResponse adresEkle(AdresRequest dto) {

        log.info("Adres ekleme isteği alındı. userId={}", dto.getAdresSahibiId());

        User user = userRepository.findById(dto.getAdresSahibiId())
                .orElseThrow(() -> {
                    log.error("User bulunamadı! userId={}", dto.getAdresSahibiId());
                    return new UserBulunamadıException("User Bulunamadı");
                });

        if (adresRepository.existsByAdresSahibi(user)) {
            log.warn("Kullanıcı zaten adrese sahip! userId={}", user.getId());
            throw new AdresZatenMevcutException("Zaten Bir Adrese Sahipsiniz");
        }

        Adres adres = new Adres();

        adres.setAdresBasligi(dto.getAdresBasligi());
        adres.setAdresSahibi(user);
        adres.setAdresTarifi(dto.getAdresTarifi());
        adres.setBinaNo(dto.getBinaNo());
        adres.setCaddeSokak(dto.getCaddeSokak());
        adres.setDaireNo(dto.getDaireNo());
        adres.setIl(dto.getIl());
        adres.setIlce(dto.getIlce());
        adres.setKatNo(dto.getKatNo());
        adres.setMahalle(dto.getMahalle());

        Adres kaydedilen = adresRepository.save(adres);

        log.info("Adres başarıyla oluşturuldu. adresId={}, userId={}", kaydedilen.getId(), user.getId());

        return new AdresResponse(
                kaydedilen.getId(),
                kaydedilen.getIl(),
                kaydedilen.getIlce(),
                kaydedilen.getMahalle(),
                kaydedilen.getCaddeSokak(),
                kaydedilen.getBinaNo(),
                kaydedilen.getKatNo(),
                kaydedilen.getDaireNo(),
                kaydedilen.getAdresTarifi(),
                kaydedilen.getAdresBasligi()
        );
    }

    public AdresResponse adresGetir(Long adresId, Long userId) {

        log.info("Adres getirme isteği. adresId={}, userId={}", adresId, userId);

        Adres adres = adresRepository.findById(adresId)
                .orElseThrow(() -> {
                    log.error("Adres bulunamadı! adresId={}", adresId);
                    return new RuntimeException("Adres bulunamadı");
                });

        log.debug("Adres bulundu. adresId={}", adres.getId());

        return new AdresResponse(
                adres.getId(),
                adres.getIl(),
                adres.getIlce(),
                adres.getMahalle(),
                adres.getCaddeSokak(),
                adres.getBinaNo(),
                adres.getKatNo(),
                adres.getDaireNo(),
                adres.getAdresTarifi(),
                adres.getAdresBasligi()
        );
    }

    @Transactional
    public AdresResponse adresGuncelle(Long adresId, Long userId, AdresRequest dto) {

        log.info("Adres güncelleme isteği. adresId={}, userId={}", adresId, userId);

        Adres adres = adresRepository.findById(adresId)
                .orElseThrow(() -> {
                    log.error("Adres bulunamadı! adresId={}", adresId);
                    return new RuntimeException("Adres bulunamadı");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User bulunamadı! userId={}", userId);
                    return new UserBulunamadıException("User bulunamadı");
                });

        if (!adres.getAdresSahibi().getId().equals(user.getId())) {
            log.warn("Yetkisiz adres güncelleme denemesi! adresId={}, userId={}", adresId, userId);
            throw new RuntimeException("Bu adresi güncelleme yetkin yok");
        }

        log.debug("Adres güncelleniyor... adresId={}", adresId);

        adres.setAdresBasligi(dto.getAdresBasligi());
        adres.setAdresTarifi(dto.getAdresTarifi());
        adres.setBinaNo(dto.getBinaNo());
        adres.setCaddeSokak(dto.getCaddeSokak());
        adres.setDaireNo(dto.getDaireNo());
        adres.setIl(dto.getIl());
        adres.setIlce(dto.getIlce());
        adres.setKatNo(dto.getKatNo());
        adres.setMahalle(dto.getMahalle());

        Adres updated = adresRepository.save(adres);

        log.info("Adres başarıyla güncellendi. adresId={}, userId={}", adresId, userId);

        return new AdresResponse(
                updated.getId(),
                updated.getIl(),
                updated.getIlce(),
                updated.getMahalle(),
                updated.getCaddeSokak(),
                updated.getBinaNo(),
                updated.getKatNo(),
                updated.getDaireNo(),
                updated.getAdresTarifi(),
                updated.getAdresBasligi()
        );
    }

    @Transactional
    public void adresSil(Long adresId, Long userId) {

        log.info("Adres silme isteği. adresId={}, userId={}", adresId, userId);

        Adres adres = adresRepository.findById(adresId)
                .orElseThrow(() -> {
                    log.error("Adres bulunamadı! adresId={}", adresId);
                    return new RuntimeException("Adres bulunamadı");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User bulunamadı! userId={}", userId);
                    return new UserBulunamadıException("User bulunamadı");
                });

        if (!adres.getAdresSahibi().getId().equals(user.getId())) {
            log.warn("Yetkisiz adres silme denemesi! adresId={}, userId={}", adresId, userId);
            throw new RuntimeException("Bu adresi silme yetkin yok");
        }

        adresRepository.delete(adres);

        log.info("Adres başarıyla silindi. adresId={}, userId={}", adresId, userId);
    }
}