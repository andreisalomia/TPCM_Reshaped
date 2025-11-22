package com.example.tpcm_spring.models.app;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "BILLCYCLEDAY", nullable = false)
    private Integer billCycleDay;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Subscriber> subscribers;
}
