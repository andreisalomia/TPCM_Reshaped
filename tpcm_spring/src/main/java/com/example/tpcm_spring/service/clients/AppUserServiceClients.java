package com.example.tpcm_spring.service.clients;

import com.example.tpcm_spring.exceptions.ConflictException;
import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.kafka.producer.KafkaProducerService;
import com.example.tpcm_spring.models.clients.AppUser;
import com.example.tpcm_spring.repository.clients.AppUserRepositoryClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceClients {

    private final AppUserRepositoryClients appUserRepository;
    private final KafkaProducerService kafkaProducerService;

    private void validateAppUser(AppUser user) {
        if (user.getUsername() == null || !user.getUsername().matches("^[A-Za-z0-9._-]{8,}$")) {
            log.warn("Validation failed for username: {}", user.getUsername());
            throw new ValidationException("Username must be at least 8 characters and contain only letters, numbers, '.', '-', '_' ");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Validation failed for password hash: {}", user.getPasswordHash());
            throw new ValidationException("Password hash must not be blank");
        }

        if (user.getRole() == null ||
                !(user.getRole().equalsIgnoreCase("ADMIN") || user.getRole().equalsIgnoreCase("USER"))) {
            log.warn("Validation failed for role: {}", user.getRole());
            throw new ValidationException("Role must be either 'ADMIN' or 'USER'");
        }

        if (user.getCreatedAt() == null) {
            log.warn("Validation failed for createdAt: {}", user.getCreatedAt());
            throw new ValidationException("createdAt must not be null");
        }
    }

    @Transactional
    public AppUser createUser(AppUser user) {
        validateAppUser(user);
        if (appUserRepository.findByUsernameIgnoreCase(user.getUsername()) != null) {
            log.warn("Attempt to create user with existing username: {}", user.getUsername());
            throw new ConflictException("Username already exists");
        }
        log.info("Creating new AppUser with username: {}", user.getUsername());
        AppUser savedUser = appUserRepository.save(user);

        kafkaProducerService.publishUserCreated(savedUser);
        log.info("AppUser created with ID: {}", savedUser.getUserID());
        return savedUser;
    }

    public List<AppUser> getAllUsers() {
        log.info("Retrieving all AppUsers");
        return appUserRepository.findAll();
    }

    public AppUser getById(Long id) {
        log.info("Retrieving AppUser with ID: {}", id);
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AppUser with ID " + id + " not found"));
    }

    @Transactional
    public AppUser updateUser(Long id, AppUser updated) {
        AppUser existing = appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AppUser with ID " + id + " not found"));

        validateAppUser(updated);

        if (!existing.getUsername().equalsIgnoreCase(updated.getUsername()) &&
                appUserRepository.findByUsernameIgnoreCase(updated.getUsername()) != null) {
            log.warn("Attempt to update user with existing username: {}", updated.getUsername());
            throw new ConflictException("Another user with this username already exists");
        }

        existing.setUsername(updated.getUsername());
        existing.setPasswordHash(updated.getPasswordHash());
        existing.setRole(updated.getRole());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setLastLogin(updated.getLastLogin());

        log.info("Updating AppUser with username: {}", existing.getUsername());
        AppUser savedUser = appUserRepository.save(existing);

        kafkaProducerService.publishUserUpdated(savedUser);
        log.info("AppUser updated with ID: {}", savedUser.getUserID());
        return savedUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing AppUser with ID: {}", id);
            throw new NotFoundException("AppUser with ID " + id + " not found");
        }
        log.info("Deleting AppUser with ID: {}", id);
        appUserRepository.deleteById(id);

        kafkaProducerService.publishUserDeleted(id);
        log.info("AppUser with ID {} deleted", id);
    }

    public AppUser findByUsername(String username) {
        if (username == null || username.isBlank()) {
            log.warn("Attempt to find user with blank username");
            throw new ValidationException("Username must not be blank");
        }
        AppUser found = appUserRepository.findByUsernameIgnoreCase(username);
        if (found == null) {
            log.warn("User with username '{}' not found", username);
            throw new NotFoundException("User with username '" + username + "' not found");
        }
        log.info("Found AppUser with username: {}", found.getUsername());
        return found;
    }
}
