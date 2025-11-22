package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateUser;
import com.example.kafkaconsumer.model.app.AppUser;
import com.example.kafkaconsumer.repository.app.AppUserRepositoryApp;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserProcessor extends AbstractMessageProcessor<CreateUser> {

    private final AppUserRepositoryApp appUserRepository;

    @Override
    @Counted("processCreateUser")
    @Timed("processCreateUser")
    @Transactional
    public void process(CreateUser createUser) {
        log.info("Processing CREATE_USER for user ID: {}", createUser.getUserID());

        try {
            if (appUserRepository.existsById(createUser.getUserID())) {
                log.warn("User with ID {} already exists in app database", createUser.getUserID());
                return;
            }

            AppUser appUser = new AppUser();
            appUser.setUserId(createUser.getUserID());
            appUser.setUsername(createUser.getUsername());
            appUser.setRole(createUser.getRole());

            appUser.setPasswordHash("SYNCED_FROM_CLIENTS");

            if (createUser.getTimestamp() != null) {
                appUser.setCreatedAt(createUser.getTimestamp());
            } else {
                appUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            appUserRepository.save(appUser);
            log.info("Successfully created user with ID {} in app database", createUser.getUserID());

        } catch (Exception e) {
            log.error("Error processing CREATE_USER for user ID: {}", createUser.getUserID(), e);
            throw new RuntimeException("Failed to process CREATE_USER event", e);
        }
    }
}