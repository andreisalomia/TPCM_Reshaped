package com.example.tpcm_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                csrf este dezactivat pentru ca nu avem frontend browser, trimited request-uri direct
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/app/transactions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/app/transactions/flow/balance/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/app/transactions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/app/transactions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/app/transactions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "api/app/thirdparties/**").hasRole("ADMIN")
                        .requestMatchers("/api/app/transactions/flow/**").hasRole("ADMIN")
                        .requestMatchers("/api/app/subscribers/**").hasRole("ADMIN")
                        .requestMatchers("/api/app/customers/**").hasRole("ADMIN")

                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/google/callback", true)
                )
                .httpBasic(httpBasic -> { /*aici ar veni configuratia de entry point */});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
