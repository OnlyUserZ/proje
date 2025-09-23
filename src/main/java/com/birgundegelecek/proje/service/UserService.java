package com.birgundegelecek.proje.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.dto.UserDTO;
import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User register(UserDTO dto) {

		if(userRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new RuntimeException("User Adı Zaten Alınmış");
		}

		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		return userRepository.save(user);


	}

	public boolean login(UserDTO dto) {
		Optional<User> user = userRepository.findByUsername(dto.getUsername());
		return user.isPresent() && passwordEncoder.matches(dto.getPassword(), user.get().getPassword());

	}
	
}
