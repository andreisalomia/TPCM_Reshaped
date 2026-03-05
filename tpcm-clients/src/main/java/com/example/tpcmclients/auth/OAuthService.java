package com.example.tpcm-clients.auth;

import com.example.tpcm_spring.models.clients.Subscriber;
import com.example.tpcm_spring.models.clients.SubscriberAuth;
import com.example.tpcm_spring.models.clients.Customer;
import com.example.tpcm_spring.repository.clients.SubscriberAuthRepository;
import com.example.tpcm_spring.repository.clients.SubscriberRepositoryClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final SubscriberAuthRepository subscriberAuthRepository;
    private final SubscriberRepositoryClients subscriberRepository;
    private final JwtService jwtService;

    public Optional<SubscriberAuth> findByProviderAndUserId(String provider, String providerUserId) {
        log.info("Searching for SubscriberAuth - Provider: {}, ProviderUserId: {}", provider, providerUserId);

        Optional<SubscriberAuth> result = subscriberAuthRepository.findByAuthProviderAndProviderUserID(provider, providerUserId);

        if (result.isPresent()) {
            log.info("Found existing SubscriberAuth for providerUserId: {}, SubscriberID: {}",
                    providerUserId, result.get().getSubscriberID());
        } else {
            log.info("No existing SubscriberAuth found for providerUserId: {}", providerUserId);
        }

        return result;
    }

    @Transactional
    public String linkMsisdnToOAuth(String msisdn, String provider, String providerUserId, String email, String displayName) {
        log.info("Starting linkMsisdnToOAuth");
        log.info("MSISDN: {}, Provider: {}, Email: {}, DisplayName: {}", msisdn, provider, email, displayName);

        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        if (subscribers.isEmpty()) {
            log.error("MSISDN not found: {}", msisdn);
            throw new RuntimeException("MSISDN not found");
        }
        Subscriber subscriber = subscribers.getFirst();
        log.info("Found subscriber - SubscriberID: {}, Status: {}", subscriber.getSubscriberID(), subscriber.getStatus());

        if (!"ACTIVE".equals(subscriber.getStatus())) {
            log.error("Subscriber is not active - Status: {}", subscriber.getStatus());
            throw new RuntimeException("Subscriber is not active");
        }

        boolean alreadyLinked = subscriberAuthRepository.existsBySubscriberID(subscriber.getSubscriberID());
        if (alreadyLinked) {
            log.error("Subscriber {} already linked to another account", subscriber.getSubscriberID());
            throw new RuntimeException("MSISDN already linked to another account");
        }
        log.info("Subscriber not yet linked, proceeding...");

        Customer customer = subscriber.getCustomer();
        log.info("Customer Email from DB: {}, OAuth Email: {}", customer.getEmail(), email);

        if (customer.getEmail() == null || !customer.getEmail().equalsIgnoreCase(email)) {
            log.error("Email mismatch - DB: {}, OAuth: {}", customer.getEmail(), email);
            throw new RuntimeException("Email does not match registered customer email");
        }
        log.info("Email validation passed");

        SubscriberAuth auth = new SubscriberAuth();
        auth.setSubscriberID(subscriber.getSubscriberID());
        auth.setAuthProvider(provider);
        auth.setProviderUserID(providerUserId);
        auth.setEmail(email);
        auth.setDisplayName(displayName);
        auth.setUserRole("USER");
        auth.setCreatedAt(LocalDateTime.now());
        auth.setLastLogin(LocalDateTime.now());

        subscriberAuthRepository.save(auth);
        log.info("Created SubscriberAuth entry for SubscriberID: {}", subscriber.getSubscriberID());

        String token = jwtService.generateToken(
                subscriber.getSubscriberID(),
                subscriber.getMsisdn(),
                email,
                displayName,
                "USER"
        );
        log.info("Generated JWT token for subscriber: {}", subscriber.getSubscriberID());
        log.info("linkMsisdnToOAuth completed successfully");

        return token;
    }

    @Transactional
    public String loginExistingUser(SubscriberAuth auth) {
        log.info("Starting loginExistingUser");
        log.info("SubscriberAuth - SubscriberID: {}, Email: {}, Provider: {}",
                auth.getSubscriberID(), auth.getEmail(), auth.getAuthProvider());

        auth.setLastLogin(LocalDateTime.now());
        subscriberAuthRepository.save(auth);
        log.info("Updated last login for SubscriberID: {}", auth.getSubscriberID());

        Subscriber subscriber = subscriberRepository.findById(auth.getSubscriberID())
                .orElseThrow(() -> {
                    log.error("Subscriber not found for SubscriberID: {}", auth.getSubscriberID());
                    return new RuntimeException("Subscriber not found");
                });
        log.info("Found subscriber - MSISDN: {}, Status: {}", subscriber.getMsisdn(), subscriber.getStatus());

        if (!"ACTIVE".equals(subscriber.getStatus())) {
            log.error("Subscriber is not active - Status: {}", subscriber.getStatus());
            throw new RuntimeException("Subscriber is not active");
        }

        String token = jwtService.generateToken(
                subscriber.getSubscriberID(),
                subscriber.getMsisdn(),
                auth.getEmail(),
                auth.getDisplayName(),
                auth.getUserRole()
        );
        log.info("Generated JWT token for existing user: {}", subscriber.getSubscriberID());
        log.info("loginExistingUser completed successfully");

        return token;
    }
}