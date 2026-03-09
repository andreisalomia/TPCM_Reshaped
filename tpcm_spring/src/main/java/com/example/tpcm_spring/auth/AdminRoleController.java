package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.app.SubscriberAuth;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @PatchMapping("/roles/{authId}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long authId,
            @RequestBody UpdateRoleRequest request
    ) {
        try {
            log.info("Received request to update role for authId: {} to role: {}", authId, request.getNewRole());

            SubscriberAuth updatedAuth = adminRoleService.updateUserRole(authId, request.getNewRole());

            String newToken = adminRoleService.generateTokenForAuth(updatedAuth);

            return ResponseEntity.ok(Map.of(
                    "message", "Role updated successfully",
                    "authId", updatedAuth.getAuthID(),
                    "subscriberId", updatedAuth.getSubscriberID(),
                    "email", updatedAuth.getEmail(),
                    "newRole", updatedAuth.getUserRole(),
                    "newToken", newToken
            ));

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error updating role: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllAuthenticatedUsers() {
        try {
            log.info("Fetching all authenticated users");
            return ResponseEntity.ok(adminRoleService.getAllUsers());
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch users"));
        }
    }

    @Data
    public static class UpdateRoleRequest {
        private String newRole;
    }
}