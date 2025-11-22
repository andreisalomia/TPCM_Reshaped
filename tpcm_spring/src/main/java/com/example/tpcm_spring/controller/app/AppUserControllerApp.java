package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.AppUser;
import com.example.tpcm_spring.service.app.AppUserServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("app.AppUserController")
@RequestMapping("/api/app/users")
@RequiredArgsConstructor
public class AppUserControllerApp {

    private final AppUserServiceApp appUserService;

    @PostMapping
    public ResponseEntity<AppUser> addUser(@RequestBody AppUser appUser) {
        AppUser saved = appUserService.addUser(appUser);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(appUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        AppUser user = appUserService.getUserById(id);
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
    public ResponseEntity<AppUser> getByUsername(@RequestParam String username) {
        AppUser user = appUserService.findByUsername(username);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-role")
    public ResponseEntity<List<AppUser>> getByRole(@RequestParam String role) {
        return ResponseEntity.ok(appUserService.findByRole(role));
    }
}
