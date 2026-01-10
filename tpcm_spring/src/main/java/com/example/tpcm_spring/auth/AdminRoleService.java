package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.clients.Subscriber;
import com.example.tpcm_spring.models.clients.SubscriberAuth;
import com.example.tpcm_spring.repository.clients.SubscriberAuthRepository;
import com.example.tpcm_spring.repository.clients.SubscriberRepositoryClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final SubscriberAuthRepository subscriberAuthRepository;
    private final SubscriberRepositoryClients subscriberRepository;
    private final JwtService jwtService;

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";

    @Transactional
    public SubscriberAuth updateUserRole(Long authId, String newRole) {
        log.info("Starting updateUserRole for authId: {}, newRole: {}", authId, newRole);

        if (!ROLE_USER.equals(newRole) && !ROLE_ADMIN.equals(newRole)) {
            log.error("Invalid role: {}", newRole);
            throw new IllegalArgumentException("Role must be either 'USER' or 'ADMIN'");
        }

        SubscriberAuth auth = subscriberAuthRepository.findById(authId)
                .orElseThrow(() -> {
                    log.error("SubscriberAuth not found for authId: {}", authId);
                    return new RuntimeException("User authentication record not found");
                });

        String oldRole = auth.getUserRole();
        log.info("Found SubscriberAuth - SubscriberID: {}, Current Role: {}, New Role: {}",
                auth.getSubscriberID(), oldRole, newRole);

        auth.setUserRole(newRole);
        SubscriberAuth savedAuth = subscriberAuthRepository.save(auth);

        log.info("Successfully updated role from {} to {} for authId: {}", oldRole, newRole, authId);

        return savedAuth;
    }

    public String generateTokenForAuth(SubscriberAuth auth) {
        log.info("Generating new token for SubscriberID: {} with role: {}",
                auth.getSubscriberID(), auth.getUserRole());

        Subscriber subscriber = subscriberRepository.findById(auth.getSubscriberID())
                .orElseThrow(() -> {
                    log.error("Subscriber not found for SubscriberID: {}", auth.getSubscriberID());
                    return new RuntimeException("Subscriber not found");
                });

        String token = jwtService.generateToken(
                auth.getSubscriberID(),
                subscriber.getMsisdn(),
                auth.getEmail(),
                auth.getDisplayName(),
                auth.getUserRole()
        );

        log.info("Generated new token for SubscriberID: {}", auth.getSubscriberID());
        return token;
    }

    public List<SubscriberAuth> getAllUsers() {
        log.info("Fetching all SubscriberAuth records");
        return subscriberAuthRepository.findAll();
    }
}