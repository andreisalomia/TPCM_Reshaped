package com.example.kafkaconsumer.model.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CUSTOMERLOGS")
public class CustomerLogs {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logID;

    @ManyToOne
    @JoinColumn(name = "CUSTOMERID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    @Column(name = "OPERATION")
    private String operation;

    @Column(name = "CHANGEDFIELD")
    private String changedField;

    @Column(name = "NEWVALUE")
    private String newValue;

    @Column(name = "OLDVALUE")
    private String oldValue;

    @Column(name = "TIMEOFCHANGE")
    private LocalDateTime timeOfChange;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AppUser user;
}
