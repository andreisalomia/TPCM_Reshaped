package com.example.kafkaconsumer.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


import java.util.Base64;

@Configuration
@Getter
@Setter
@Slf4j
public class TpcmApiConfig {

    @Value("${tpcm.api.base-url}")
    private String baseUrl;

    @Value("${tpcm.api.username}")
    private String username;

    @Value("${tpcm.api.password}")
    private String password;

    @Bean
    public WebClient tpcmWebClient() {
        log.info("Creating WebClient with baseUrl: {}", baseUrl);

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
