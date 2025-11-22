package com.example.kafkaconsumer.repository.app;

import com.example.kafkaconsumer.model.app.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("appSubscriberRepository")
public interface SubscriberRepositoryApp extends JpaRepository<Subscriber, Long> {
    int countByCustomerCustomerID(Long customerId);
    boolean existsByMsisdn(String msisdn);
    List<Subscriber> findByMsisdn(String msisdn);
    List<Subscriber> findBySubscriptionTypeIgnoreCase(String subscriptionType);
}