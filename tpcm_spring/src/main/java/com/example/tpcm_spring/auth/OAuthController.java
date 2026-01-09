package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.clients.SubscriberAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/user")
    public ResponseEntity<?> getOAuthUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String providerUserId = principal.getAttribute("sub");
        String email = principal.getAttribute("email");
        String displayName = principal.getAttribute("name");

        Optional<SubscriberAuth> existing = oAuthService.findByProviderAndUserId("GOOGLE", providerUserId);

        if (existing.isPresent()) {
            String token = oAuthService.loginExistingUser(existing.get());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "newUser", false
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "newUser", true,
                    "providerUserId", providerUserId,
                    "email", email,
                    "displayName", displayName
            ));
        }
    }

    @PostMapping("/link-msisdn")
    public ResponseEntity<?> linkMsisdn(@RequestBody LinkMsisdnRequest request) {
        try {
            String token = oAuthService.linkMsisdnToOAuth(
                    request.getMsisdn(),
                    "GOOGLE",
                    request.getProviderUserId(),
                    request.getEmail(),
                    request.getDisplayName()
            );

            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @lombok.Data
    public static class LinkMsisdnRequest {
        private String msisdn;
        private String providerUserId;
        private String email;
        private String displayName;
    }
}