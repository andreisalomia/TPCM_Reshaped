package com.example.tpcm_spring.repository.clients;

import com.example.tpcm_spring.models.clients.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepositoryClients extends JpaRepository<Subscriber, Long> {

    List<Subscriber> findByMsisdn(String msisdn);

    List<Subscriber> findBySubscriptionTypeIgnoreCase(String subscriptionType);

    int countByCustomerCustomerID(Long customerCustomerID);

    boolean existsByMsisdn(String msisdn);
}
