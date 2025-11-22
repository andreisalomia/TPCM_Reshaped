package com.example.tpcm_spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberUpdateEvent {
    private String eventType; // CREATE, UPDATE_MSISDN, UPDATE_STATUS, UPDATE_SUBSCRIPTION_TYPE, UPDATE_CUSTOMER, DELETE
    private Long subscriberID;
    private String msisdn;
    private String status;
    private String subscriptionType;
    private Long customerID;

    private String changedField;
    private String oldValue;
    private String newValue;
    private Long oldCustomerID;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String source = "CLIENTS_SERVICE";

    public static SubscriberUpdateEvent createEvent(Long subscriberID, String msisdn, String status, String subscriptionType, Long customerID) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("CREATE");
        event.setSubscriberID(subscriberID);
        event.setMsisdn(msisdn);
        event.setStatus(status);
        event.setSubscriptionType(subscriptionType);
        event.setCustomerID(customerID);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static SubscriberUpdateEvent msisdnUpdateEvent(Long subscriberID, String oldMsisdn, String newMsisdn) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("UPDATE_MSISDN");
        event.setSubscriberID(subscriberID);
        event.setChangedField("msisdn");
        event.setOldValue(oldMsisdn);
        event.setNewValue(newMsisdn);
        event.setMsisdn(newMsisdn);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static SubscriberUpdateEvent statusUpdateEvent(Long subscriberID, String oldStatus, String newStatus) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("UPDATE_STATUS");
        event.setSubscriberID(subscriberID);
        event.setChangedField("status");
        event.setOldValue(oldStatus);
        event.setNewValue(newStatus);
        event.setStatus(newStatus);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static SubscriberUpdateEvent subscriptionTypeUpdateEvent(Long subscriberID, String oldType, String newType) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("UPDATE_SUBSCRIPTION_TYPE");
        event.setSubscriberID(subscriberID);
        event.setChangedField("subscriptionType");
        event.setOldValue(oldType);
        event.setNewValue(newType);
        event.setSubscriptionType(newType);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static SubscriberUpdateEvent customerUpdateEvent(Long subscriberID, Long oldCustomerID, Long newCustomerID) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("UPDATE_CUSTOMER");
        event.setSubscriberID(subscriberID);
        event.setChangedField("customerID");
        event.setOldCustomerID(oldCustomerID);
        event.setCustomerID(newCustomerID);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static SubscriberUpdateEvent deleteEvent(Long subscriberID) {
        SubscriberUpdateEvent event = new SubscriberUpdateEvent();
        event.setEventType("DELETE");
        event.setSubscriberID(subscriberID);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
