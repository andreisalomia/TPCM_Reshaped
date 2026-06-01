package com.example.tpcm_spring.auth;

import com.example.tpcm_spring.models.app.SubscriberAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthService oAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("OAuth Success Handler Called");

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();


        String providerUserId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String displayName = oAuth2User.getAttribute("name");

        log.info("OAuth User Details - Email: {}, Name: {}, ProviderUserId: {}", email, displayName, providerUserId);

        Optional<SubscriberAuth> existing = oAuthService.findByProviderAndUserId("GOOGLE", providerUserId);


            if (existing.isPresent()) {
                log.info("Existing user found for providerUserId: {}", providerUserId);

                try {
                    String token = oAuthService.loginExistingUser(existing.get());
                    String redirectUrl = "http://localhost:3000/auth/callback?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

                    log.info("Redirecting existing user to: {}", redirectUrl);
                    getRedirectStrategy().sendRedirect(request, response, redirectUrl);

                } catch (Exception e) {
                    log.error("Error generating token for existing user: {}", e.getMessage(), e);
                    String errorUrl = "http://localhost:3000/login?error=token_generation_failed";
                    getRedirectStrategy().sendRedirect(request, response, errorUrl);
                }

            } else {
                log.info("New user detected - Email: {}, redirecting to link-msisdn", email);

                String redirectUrl = String.format(
                        "http://localhost:3000/link-msisdn?providerUserId=%s&email=%s&displayName=%s",
                        URLEncoder.encode(providerUserId, StandardCharsets.UTF_8),
                        URLEncoder.encode(email, StandardCharsets.UTF_8),
                        URLEncoder.encode(displayName, StandardCharsets.UTF_8)
                );

                log.info("Redirecting new user to: {}", redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            }

            log.info("OAuth Success Handler Completed Successfully");

        } catch (Exception e) {
            log.error("CRITICAL ERROR in OAuth Success Handler", e);
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());

            try {
                String errorUrl = "http://localhost:3000/login?error=oauth_handler_failed";
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
            } catch (IOException redirectError) {
                log.error("Failed to redirect after error", redirectError);
            }
        }
    }
}