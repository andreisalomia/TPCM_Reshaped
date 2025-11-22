package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateUser;
import com.example.kafkaconsumer.model.app.AppUser;
import com.example.kafkaconsumer.repository.app.AppUserRepositoryApp;
import com.example.kafkaconsumer.repository.clients.AppUserRepositoryClients;
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
public class UpdateUserProcessor extends AbstractMessageProcessor<UpdateUser> {

    private final AppUserRepositoryApp appUserRepositoryApp;
    private final AppUserRepositoryClients appUserRepositoryClients;

    @Override
    @Counted("processUpdateUser")
    @Timed("processUpdateUser")
    @Transactional
    public void process(UpdateUser updateUser) {
        log.info("Processing UPDATE_USER for user ID: {}", updateUser.getUserID());

        try {
            AppUser appUser = appUserRepositoryApp.findById(updateUser.getUserID())
                    .orElseGet(() -> {
                        log.info("User {} not found in app database, importing from clients database",
                                updateUser.getUserID());
                        return importUserFromClients(updateUser.getUserID());
                    });

            if (appUser == null) {
                log.error("User {} not found in either database", updateUser.getUserID());
                return;
            }

            if (updateUser.getUsername() != null) {
                appUser.setUsername(updateUser.getUsername());
            }
            if (updateUser.getRole() != null) {
                appUser.setRole(updateUser.getRole());
            }

            appUserRepositoryApp.save(appUser);

            log.info("Successfully updated user with ID {}", updateUser.getUserID());

        } catch (Exception e) {
            log.error("Error processing UPDATE_USER for user ID: {}",
                    updateUser.getUserID(), e);
            throw new RuntimeException("Failed to process UPDATE_USER event", e);
        }
    }

    private AppUser importUserFromClients(Long userId) {
        return appUserRepositoryClients.findById(userId)
                .map(clientsUser -> {
                    AppUser appUser = new AppUser();
                    appUser.setUserId(clientsUser.getUserID());
                    appUser.setUsername(clientsUser.getUsername());
                    appUser.setPasswordHash(clientsUser.getPasswordHash());
                    appUser.setRole(clientsUser.getRole());

                    if (clientsUser.getCreatedAt() != null) {
                        appUser.setCreatedAt(Timestamp.valueOf(clientsUser.getCreatedAt()));
                    }
                    if (clientsUser.getLastLogin() != null) {
                        appUser.setLastLogin(Timestamp.valueOf(clientsUser.getLastLogin()));
                    }

                    return appUserRepositoryApp.save(appUser);
                })
                .orElse(null);
    }
}