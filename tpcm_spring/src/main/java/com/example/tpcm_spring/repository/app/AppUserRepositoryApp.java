package com.example.tpcm_spring.repository.app;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tpcm_spring.models.app.AppUser;

import java.util.List;

@Repository
public interface AppUserRepositoryApp extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    List<AppUser> findByRoleIgnoreCase(String role);
}
