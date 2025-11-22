package com.example.kafkaconsumer.repository.clients;

import com.example.kafkaconsumer.model.clients.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("clientsSubscriberRepository")
public interface SubscriberRepositoryClients extends JpaRepository<Subscriber, Long> {
}