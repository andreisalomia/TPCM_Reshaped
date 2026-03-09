package com.example.kafkaconsumer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateCustomerEmail extends BaseTopicEntry {
    private Long customerID;
    private String email;
}