package com.example.tpcm_clients.service.clients;

import com.example.tpcm_clients.models.clients.AppUser;
import com.example.tpcm_clients.repository.clients.AppUserRepositoryClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepositoryClients appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsernameIgnoreCase(username);

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .roles(appUser.getRole())
                .build();
    }
}