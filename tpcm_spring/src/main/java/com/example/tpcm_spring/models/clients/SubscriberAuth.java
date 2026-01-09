package com.example.tpcm_spring.models.clients;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SUBSCRIBERAUTH")
public class SubscriberAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHID")
    private Long authID;

    @Column(name = "SUBSCRIBERID", nullable = false)
    private Long subscriberID;

    @Column(name = "AUTHPROVIDER", nullable = false, length = 20)
    private String authProvider;

    @Column(name = "PROVIDERUSERID", nullable = false)
    private String providerUserID;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "DISPLAYNAME")
    private String displayName;

    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @Column(name = "LASTLOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "USERROLE", length = 20)
    private String userRole;
}