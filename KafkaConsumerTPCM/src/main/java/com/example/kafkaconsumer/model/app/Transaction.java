package com.example.kafkaconsumer.model.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Transaction")
public class Transaction {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTIONID")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "SUBSCRIBERID", nullable = false)
    @JsonIgnoreProperties({"transactions"})
    private Subscriber subscriber;

    @Column(name = "CREATEDDATE")
    private Timestamp createdDate;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "CHANNEL")
//    @Pattern(regexp = "^(SMS|CALL|APP)$", message = "Channel must be either 'SMS', 'CALL', or 'APP'")
    private String channel;

    @Column(name = "MSISDN", nullable = false)
    private String msisdn;

    @ManyToOne
    @JoinColumn(name = "TPID")
    private ThirdParty thirdParty;

    @Column(name = "STATUS")
//    @Pattern(regexp = "^(PENDING|COMMITTED|FAILED|COMPLETED)$", message = "Status must be either 'PENDING', 'COMMITTED', 'FAILED', or 'COMPLETED'")
    private String status;

    @Column(name = "PARTIALRESERVATION", length = 1)
//    @Pattern(regexp = "^[YN]$", message = "IsCounted must be either 'Y' or 'N'")
    private String partialReservation = "N";

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"transaction", "subscriber"})
    private List<LogEvent> logEvents;
}
