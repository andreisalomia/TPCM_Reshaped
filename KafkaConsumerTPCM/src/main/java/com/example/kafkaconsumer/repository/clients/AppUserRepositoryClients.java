package com.example.kafkaconsumer.repository.clients;

import com.example.kafkaconsumer.model.clients.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("clientsUserRepository")
public interface AppUserRepositoryClients extends JpaRepository<AppUser, Long> {
}