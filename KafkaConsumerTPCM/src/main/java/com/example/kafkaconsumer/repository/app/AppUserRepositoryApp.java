package com.example.kafkaconsumer.repository.app;

import com.example.kafkaconsumer.model.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("appUserRepository")
public interface AppUserRepositoryApp extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    List<AppUser> findByRoleIgnoreCase(String role);
}