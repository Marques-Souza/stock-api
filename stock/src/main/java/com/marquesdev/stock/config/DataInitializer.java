package com.marquesdev.stock.config;

import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import com.marquesdev.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findByEmail("admin@stock.com").isPresent()) {
            return;
        }

        User admin = new User();
        admin.setEmail("admin@stock.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setProfile(Profile.ADMIN);

        userRepository.save(admin);
        log.info("Usuario admin criado automaticamente: admin@stock.com / admin123");
    }
}
