package com.marquesdev.stock.config;

import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import com.marquesdev.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.email:admin@stock.com}")
    private String email;

    @Value("${app.bootstrap-admin.password:admin123}")
    private String password;

    @PostConstruct
    public void init() {
        if (!enabled) {
            return;
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("Bootstrap admin credentials must be configured when enabled.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setProfile(Profile.ADMIN);

        userRepository.save(admin);
        log.info("Bootstrap administrator created for {}", email);
    }
}
