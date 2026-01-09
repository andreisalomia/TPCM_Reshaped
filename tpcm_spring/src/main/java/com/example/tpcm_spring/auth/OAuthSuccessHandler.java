package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.clients.SubscriberAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthService oAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerUserId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String displayName = oAuth2User.getAttribute("name");

        Optional<SubscriberAuth> existing = oAuthService.findByProviderAndUserId("GOOGLE", providerUserId);

        if (existing.isPresent()) {
            String token = oAuthService.loginExistingUser(existing.get());
            String redirectUrl = "http://localhost:3000/auth/callback?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            String redirectUrl = String.format(
                    "http://localhost:3000/link-msisdn?providerUserId=%s&email=%s&displayName=%s",
                    URLEncoder.encode(providerUserId, StandardCharsets.UTF_8),
                    URLEncoder.encode(email, StandardCharsets.UTF_8),
                    URLEncoder.encode(displayName, StandardCharsets.UTF_8)
            );
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}