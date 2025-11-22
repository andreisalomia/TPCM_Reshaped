package com.example.kafkaconsumer.model.clients;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "APPUSER")
public class AppUser {
    @Id
    private Long userID;

    @Column(nullable = false, unique = true, name= "USERNAME")
//    @Pattern(regexp = "^[A-Za-z0-9._-]{8,}$", message = "Username must be at least 8 characters long and can't contain special characters)")
    private String username;

    @Column(nullable = false, name= "PASSWORDHASH")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
//            message = "Password must be at least 8 characters long, must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String passwordHash;

    @Column(nullable = false, name= "ROLE")
//    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role must be either 'ADMIN' or 'USER'")
    private String role;

    @Column(nullable = false, name= "CREATEDAT")
    private LocalDateTime createdAt;

    @Column(name = "LASTLOGIN")
    private LocalDateTime lastLogin;
}
