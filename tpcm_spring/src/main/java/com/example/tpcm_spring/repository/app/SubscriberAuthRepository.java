package com.example.tpcm_spring.repository.app;

import com.example.tpcm_spring.models.app.SubscriberAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberAuthRepository extends JpaRepository<SubscriberAuth, Long> {
    Optional<SubscriberAuth> findByAuthProviderAndProviderUserID(String authProvider, String providerUserID);
    boolean existsBySubscriberID(Long subscriberID);
}