package com.example.tpcm_spring.models.app;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "THIRDPARTY")
public class ThirdParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "THIRDPARTYID")
    private Long thirdPartyID;

    @Column(name = "NAME", nullable = false)
//    @Pattern(regexp = "^[a-zA-Z0-9 ]{1,100}$", message = "Name must be alphanumeric and up to 100 characters long")
    private String name;

    @Column(name = "SERVICETYPE", nullable = false)
//    @Pattern(regexp = "^[a-zA-Z ]{1,50}$", message = "Service Type must be alphabetic and up to 50 characters long")
    private String serviceType;

    @Column(name = "EMAIL")
//    @Email
    private String email;

    @Column(name = "ACTIVE", length = 1)
//    @Pattern(regexp = "^[YN]$", message = "Active must be either 'Y' or 'N'")
    private String active;
}
