package com.example.tpcm_clients.config;

import com.example.tpcm_clients.models.clients.AppUser;
import com.example.tpcm_clients.repository.clients.AppUserRepositoryClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepositoryClients userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.init.admin.username}") private String adminUsername;
    @Value("${app.init.admin.password}") private String adminPassword;
    @Value("${app.init.default-user.username}") private String defaultUsername;
    @Value("${app.init.default-user.password}") private String defaultPassword;
    @Value("${app.init.default-user.role}") private String defaultUserRole;
    @Value("${app.init.default-admin.role}") private String defaultAdminRole;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            AppUser adminUser = new AppUser();
            adminUser.setUsername(adminUsername);
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            adminUser.setRole(defaultAdminRole);
            adminUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(adminUser);

            AppUser defaultUser = new AppUser();
            defaultUser.setUsername(defaultUsername);
            defaultUser.setPasswordHash(passwordEncoder.encode(defaultPassword));
            defaultUser.setRole(defaultUserRole);
            defaultUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(defaultUser);
        }
    }
}