package com.birgundegelecek.proje.service;

import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.repository.UserSepetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class SiparisService {
	
	private final UserSepetRepository userSepetRepository;

}
