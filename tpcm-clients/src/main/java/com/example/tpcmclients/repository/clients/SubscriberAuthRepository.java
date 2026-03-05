package com.example.tpcm-clients.repository.clients;

import com.example.tpcm_spring.models.clients.SubscriberAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberAuthRepository extends JpaRepository<SubscriberAuth, Long> {
    Optional<SubscriberAuth> findByAuthProviderAndProviderUserID(String authProvider, String providerUserID);
    boolean existsBySubscriberID(Long subscriberID);
}