package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.clients.Subscriber;
import com.example.tpcm_spring.models.clients.SubscriberAuth;
import com.example.tpcm_spring.models.clients.Customer;
import com.example.tpcm_spring.repository.clients.SubscriberAuthRepository;
import com.example.tpcm_spring.repository.clients.SubscriberRepositoryClients;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final SubscriberAuthRepository subscriberAuthRepository;
    private final SubscriberRepositoryClients subscriberRepository;
    private final JwtService jwtService;

    public Optional<SubscriberAuth> findByProviderAndUserId(String provider, String providerUserId) {
        return subscriberAuthRepository.findByAuthProviderAndProviderUserID(provider, providerUserId);
    }

    @Transactional
    public String linkMsisdnToOAuth(String msisdn, String provider, String providerUserId, String email, String displayName) {

        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        if (subscribers.isEmpty()) {
            throw new RuntimeException("MSISDN not found");
        }
        Subscriber subscriber = subscribers.getFirst();

        if (!"ACTIVE".equals(subscriber.getStatus())) {
            throw new RuntimeException("Subscriber is not active");
        }

        if (subscriberAuthRepository.existsBySubscriberID(subscriber.getSubscriberID())) {
            throw new RuntimeException("MSISDN already linked to another account");
        }

        Customer customer = subscriber.getCustomer();
        if (customer.getEmail() == null || !customer.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Email does not match registered customer email");
        }

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

        return jwtService.generateToken(subscriber.getSubscriberID(), subscriber.getMsisdn(), email, displayName, "USER");
    }

    @Transactional
    public String loginExistingUser(SubscriberAuth auth) {
        auth.setLastLogin(LocalDateTime.now());
        subscriberAuthRepository.save(auth);

        Subscriber subscriber = subscriberRepository.findById(auth.getSubscriberID()).orElseThrow(() -> new RuntimeException("Subscriber not found"));

        if (!"ACTIVE".equals(subscriber.getStatus())) {
            throw new RuntimeException("Subscriber is not active");
        }

        return jwtService.generateToken(subscriber.getSubscriberID(), subscriber.getMsisdn(), auth.getEmail(), auth.getDisplayName(), auth.getUserRole());
    }
}