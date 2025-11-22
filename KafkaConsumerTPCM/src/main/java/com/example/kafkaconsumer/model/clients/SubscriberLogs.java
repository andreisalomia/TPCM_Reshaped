package com.example.kafkaconsumer.model.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SUBSCRIBERLOGS")
public class SubscriberLogs {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logID;

    @ManyToOne
    @JoinColumn(name = "SUBSCRIBERID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Subscriber subscriber;

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
