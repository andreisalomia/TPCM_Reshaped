package com.example.tpcm_spring.config;

import com.example.tpcm_spring.models.app.AppUser;
import com.example.tpcm_spring.repository.app.AppUserRepositoryApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepositoryApp userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            AppUser adminUser = new AppUser();
            adminUser.setUsername("admin");
            adminUser.setPasswordHash(passwordEncoder.encode("admin"));
            adminUser.setRole("ADMIN");
            userRepository.save(adminUser);
            if (userRepository.findByUsername("admin") == null) {
                throw new Exception("Admin user not created successfully");
            }

            AppUser defaultUser = new AppUser();
            defaultUser.setUsername("user");
            defaultUser.setPasswordHash(passwordEncoder.encode("user"));
            defaultUser.setRole("USER");
            userRepository.save(defaultUser);
            if (userRepository.findByUsername("user") == null) {
                throw new Exception("Default user not created successfully");
            }
        }
    }
}
