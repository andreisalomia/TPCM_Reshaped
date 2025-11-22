package com.example.tpcm_spring.repository.app;

import com.example.tpcm_spring.models.app.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepositoryApp extends JpaRepository<Subscriber, Long> {
    int countByCustomerCustomerID(Long customerId);
    boolean existsByMsisdn(String msisdn);
    List<Subscriber> findByMsisdn(String msisdn);
    List<Subscriber> findBySubscriptionTypeIgnoreCase(String subscriptionType);
}
