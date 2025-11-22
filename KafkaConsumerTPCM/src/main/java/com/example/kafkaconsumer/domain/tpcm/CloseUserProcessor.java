package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseUser;
import com.example.kafkaconsumer.model.app.AppUser;
import com.example.kafkaconsumer.repository.app.AppUserRepositoryApp;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseUserProcessor extends AbstractMessageProcessor<CloseUser> {

    private final AppUserRepositoryApp appUserRepository;

    @Override
    @Counted("processCloseUser")
    @Timed("processCloseUser")
    @Transactional
    public void process(CloseUser closeUser) {
        log.info("Processing CLOSE_USER for user ID: {}", closeUser.getUserID());

        try {
            AppUser appUser = appUserRepository.findById(closeUser.getUserID()).orElse(null);

            if (appUser == null) {
                log.warn("User with ID {} not found in app database, nothing to delete",
                        closeUser.getUserID());
                return;
            }

            appUserRepository.delete(appUser);
            log.info("Successfully deleted user with ID {} from app database",
                    closeUser.getUserID());

        } catch (Exception e) {
            log.error("Error processing CLOSE_USER for user ID: {}",
                    closeUser.getUserID(), e);
            throw new RuntimeException("Failed to process CLOSE_USER event", e);
        }
    }
}