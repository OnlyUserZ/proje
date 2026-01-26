package com.birgundegelecek.proje;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.birgundegelecek.proje.entity.User;
import com.birgundegelecek.proje.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
    	

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Kullanıcı bulunamadı")
                );

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole())
                )
        );
    }
}
