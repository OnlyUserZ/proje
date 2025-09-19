package com.birgundegelecek.proje;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    private final PasswordEncoder passwordEncoder;

    public UserConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin1 = User.builder()
            .username("admin1")
            .password(passwordEncoder.encode("1562"))
            .roles("ADMIN")
            .build();

        UserDetails admin2 = User.builder()
            .username("admin2")
            .password(passwordEncoder.encode("1564"))
            .roles("ADMIN")
            .build();

        UserDetails admin3 = User.builder()
            .username("admin3")
            .password(passwordEncoder.encode("1566"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin1, admin2, admin3);
    }
}
