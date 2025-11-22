package com.example.kafkaconsumer.model.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Subscriber")
public class Subscriber {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUBSCRIBERID", nullable = false, unique = true)
    private Long subscriberID;

    @Column(name = "MSISDN", nullable = false)
//    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "MSISDN must be a valid phone number")
    private String msisdn;

    @Column(name = "STATUS")
//    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "Status must be either 'ACTIVE', 'INACTIVE', or 'SUSPENDED'")
    private String status;

    @Column(name = "SUBSCRIPTIONTYPE")
//    @Pattern(regexp = "^(PREPAID|POSTPAID|HYBRID)$", message = "Subscription type must be either 'PREPAID', 'POSTPAID' or 'HYBRID'")
    private String subscriptionType;

    @ManyToOne
    @JoinColumn(name = "CUSTOMERID", nullable = false)
    @JsonIgnoreProperties({"subscribers"})
    private Customer customer;

    @OneToOne(mappedBy = "subscriber", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"subscriber"})
    private Limit limit;

    @OneToMany(mappedBy = "subscriber")
    @JsonIgnoreProperties({"subscriber", "logEvents"})
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "subscriber")
    @JsonIgnoreProperties({"subscriber", "transaction"})
    private List<LogEvent> logEvents;
}
