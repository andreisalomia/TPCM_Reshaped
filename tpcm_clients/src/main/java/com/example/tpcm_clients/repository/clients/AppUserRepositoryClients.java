package com.example.tpcm_clients.repository.clients;

import com.example.tpcm_clients.models.clients.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepositoryClients extends JpaRepository<AppUser, Long> {
    AppUser findByUsernameIgnoreCase(String username);
}
