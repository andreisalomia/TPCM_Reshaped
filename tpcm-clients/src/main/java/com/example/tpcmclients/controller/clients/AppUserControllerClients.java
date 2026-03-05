package com.example.tpcm-clients.controller.clients;

import com.example.tpcm_spring.models.clients.AppUser;
import com.example.tpcm_spring.service.clients.AppUserServiceClients;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("clients.AppUserController")
@RequestMapping("/api/clients/app-users")
@RequiredArgsConstructor
public class AppUserControllerClients {

    private final AppUserServiceClients appUserService;

    @PostMapping
    public ResponseEntity<AppUser> createUserResponse(@RequestBody AppUser user) {
        AppUser saved = appUserService.createUser(user);
        return (saved != null) ? ResponseEntity.status(201).body(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = appUserService.getAllUsers();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserByIdResponse(@PathVariable Long id) {
        AppUser user = appUserService.getById(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser updated) {
        AppUser user = appUserService.updateUser(id, updated);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-username")
    public ResponseEntity<AppUser> findByUsername(@RequestParam("username") String username) {
        AppUser user = appUserService.findByUsername(username);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}
