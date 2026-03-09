package com.example.tpcm_clients.models.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Subscriber")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriberID;

    @Column(name = "MSISDN", nullable = false, unique = true)
//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "MSISDN must be a valid phone number")
    private String msisdn;

    @Column(name = "STATUS")
//    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "Status must be either 'ACTIVE', 'INACTIVE', or 'SUSPENDED'")
    private String status;

    @Column(name = "SUBSCRIPTIONTYPE", nullable = false)
//    @Pattern(regexp = "^(PREPAID|POSTPAID|HYBRID)$", message = "Subscription type must be either 'PREPAID', 'POSTPAID' or 'HYBRID'")
    private String subscriptionType;

    @ManyToOne
    @JoinColumn(name = "CUSTOMERID", nullable = false)
    @JsonIgnoreProperties({"subscribers", "customerLogs"})
    private Customer customer;
}
