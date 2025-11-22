package com.example.tpcm_spring.models.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Limit")
public class Limit {
    @Id
    @Column(name = "SUBSCRIBERID")
    private Long subscriberID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "SUBSCRIBERID")
    @JsonIgnoreProperties("limit")
    private Subscriber subscriber;

    @Column(name = "CONSUMEDAMOUNT")
//    @Min(0)
//    @Max(300)
    private Double consumedAmount;

    @Column(name = "LASTRESET")
    private Timestamp lastReset;

    @Column(name = "MAXAMOUNTCYCLE")
    private Integer maxAmountCycle = 300;

    @Column(name = "MAXAMOUNTTRANSACTION")
    private Integer maxAmountTransaction = 50;
}
