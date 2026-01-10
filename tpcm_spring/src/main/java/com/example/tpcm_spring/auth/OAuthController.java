package com.example.tpcm_spring.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OAuthController {

    private final OAuthService oAuthService;

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