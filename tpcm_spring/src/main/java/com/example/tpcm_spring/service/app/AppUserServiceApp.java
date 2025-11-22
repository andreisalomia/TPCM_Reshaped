package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.ConflictException;
import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.AppUser;
import com.example.tpcm_spring.repository.app.AppUserRepositoryApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceApp {

    private final AppUserRepositoryApp appUserRepository;
    private static final List<String> VALID_ROLES = Arrays.asList("ADMIN", "USER");

    private void validateAppUser(AppUser user) {
        if (user.getUsername() == null || !user.getUsername().matches("^[A-Za-z0-9._-]{8,}$")) {
            log.warn("Invalid username: {}", user.getUsername());
            throw new ValidationException("Username must be at least 8 characters and contain only letters, digits, '.', '-', '_' ");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Password hash must not be blank");
            throw new ValidationException("Password hash must not be blank");
        }

        if (user.getRole() == null || !VALID_ROLES.contains(user.getRole().toUpperCase())) {
            log.warn("Invalid role: {}", user.getRole());
            throw new ValidationException("Role must be either 'ADMIN' or 'USER'");
        }

        if (user.getCreatedAt() == null) {
            log.warn("createdAt must not be null");
            throw new ValidationException("createdAt must not be null");
        }
    }

    @Transactional
    public AppUser addUser(AppUser user) {
        validateAppUser(user);

        if (appUserRepository.findByUsername(user.getUsername()) != null) {
            log.warn("Conflict: Username '{}' already exists", user.getUsername());
            throw new ConflictException("Username already exists");
        }

        AppUser saved = appUserRepository.save(user);
        log.info("Created AppUser with ID: {}", saved.getUserId());
        return saved;
    }

    public List<AppUser> getAllUsers() {
        log.info("Fetching all app users");
        return appUserRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AppUser with ID " + id + " not found"));
    }

    @Transactional
    public AppUser updateUser(Long id, AppUser updated) {
        AppUser existing = appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AppUser with ID " + id + " not found"));

        validateAppUser(updated);

        if (!existing.getUsername().equalsIgnoreCase(updated.getUsername()) &&
                appUserRepository.findByUsername(updated.getUsername()) != null) {
            log.warn("Conflict: Another user with username '{}' already exists", updated.getUsername());
            throw new ConflictException("Another user with this username already exists");
        }

        existing.setUsername(updated.getUsername());
        existing.setPasswordHash(updated.getPasswordHash());
        existing.setRole(updated.getRole());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setLastLogin(updated.getLastLogin());

        AppUser saved = appUserRepository.save(existing);
        log.info("Updated AppUser with ID: {}", saved.getUserId());
        return saved;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing user with ID: {}", id);
            throw new NotFoundException("AppUser with ID " + id + " not found");
        }
        appUserRepository.deleteById(id);
        log.info("Deleted AppUser with ID: {}", id);
    }

    public AppUser findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username must not be blank");
        }
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) {
            log.warn("User with username '{}' not found", username);
            throw new NotFoundException("User with username '" + username + "' not found");
        }
        log.info("Found AppUser with username: {}", username);
        return user;
    }

    public List<AppUser> findByRole(String role) {
        if (role == null || role.isBlank()) {
            log.warn("Role must not be blank");
            throw new ValidationException("Role must not be blank");
        }
        if (!VALID_ROLES.contains(role.toUpperCase())) {
            log.warn("Invalid role: {}", role);
            throw new ValidationException("Role must be one of: " + VALID_ROLES);
        }
        log.info("Finding AppUsers with role: {}", role);
        return appUserRepository.findByRoleIgnoreCase(role);
    }
}
