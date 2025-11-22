package com.example.kafkaconsumer.model.app;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "APPUSER")
public class AppUser {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    private Long userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
//    @Pattern(regexp = "^[A-Za-z0-9._-]{8,}$", message = "Username must be at least 8 characters long and can't contain special characters)")
    private String username;

    @Column(name = "PASSWORDHASH", nullable = false)
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
//            message = "Password must be at least 8 characters long, must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String passwordHash;

    @Column(name = "ROLE", nullable = false)
//    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role must be either 'ADMIN' or 'USER'")
    private String role;

    @Column(name = "CREATEDAT")
    private Timestamp createdAt;

    @Column(name = "LASTLOGIN")
    private Timestamp lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogEvent> logEvents;
}
