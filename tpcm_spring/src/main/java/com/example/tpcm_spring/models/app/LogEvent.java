package com.example.tpcm_spring.models.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LOGEVENT")
public class LogEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENTID")
    private Long eventId;

    @Column(name = "EVENTTYPE")
    private String eventType;

    @ManyToOne
    @JoinColumn(name = "TRANSACTIONID")
    @JsonIgnoreProperties({"logEvents", "subscriber"})
    private Transaction transaction;

    @Column(name = "LOGTIMESTAMP")
    private Timestamp logTimestamp;

    @ManyToOne
    @JoinColumn(name = "SUBSCRIBERID")
    @JsonIgnoreProperties({"logEvents", "transactions"})
    private Subscriber subscriber;

    @ManyToOne
    @JoinColumn(name = "USERID")
    @JsonIgnoreProperties({"logEvents"})
    private AppUser user;

    @Column(name = "DETAILEDEVENT", length = 4000)
    private String detailedEvent;
}
