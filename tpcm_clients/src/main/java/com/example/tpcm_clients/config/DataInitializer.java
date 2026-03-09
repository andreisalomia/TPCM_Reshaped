package com.example.tpcm_clients.config;

import com.example.tpcm_clients.models.clients.AppUser;
import com.example.tpcm_clients.repository.clients.AppUserRepositoryClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepositoryClients userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            AppUser adminUser = new AppUser();
            adminUser.setUsername("admin");
            adminUser.setPasswordHash(passwordEncoder.encode("admin"));
            adminUser.setRole("ADMIN");
            adminUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(adminUser);

            AppUser defaultUser = new AppUser();
            defaultUser.setUsername("user");
            defaultUser.setPasswordHash(passwordEncoder.encode("user"));
            defaultUser.setRole("USER");
            defaultUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(defaultUser);
        }
    }
}