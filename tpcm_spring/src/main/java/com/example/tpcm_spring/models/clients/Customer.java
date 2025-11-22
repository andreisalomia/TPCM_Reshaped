package com.example.tpcm_spring.models.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Customer")

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMERID", nullable = false, unique = true)
    private Long customerID;

    @Column(name = "NAME", nullable = false)
//    @Pattern(regexp = "^[A-Za-z -']+$", message = "Name must contain only letters and spaces")
    private String name;

    @Column(name = "TYPE", nullable = false)
//    @Pattern(regexp = "^(Individual|SME|Large Enterprise)$", message = "Type must be one of: Individual, SME, Large Enterprise")
    private String type;

    @Column(name = "NRSUBSCRIBERS")
    private Integer nrSubscribers;

//    @Min(1)
//    @Max(31)
    @Column(name = "BILLCYCLEDAY")
    private Integer billCycleDay;

    @Column(name = "EMAIL")
//    @Email
    private String email;

    @Column(name = "CONTACTNUMBER")
//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Contact number must be a valid phone number")
    private String contactNumber;

    @Column(name = "ADDRESS")
//    @Pattern(regexp = "^[A-Za-z0-9 ,.-]+$", message = "Address must contain only letters, numbers, spaces, and basic punctuation")
    private String address;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch =  FetchType.LAZY)
    private List<Subscriber> subscribers;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerLogs> customerLogs;
}
