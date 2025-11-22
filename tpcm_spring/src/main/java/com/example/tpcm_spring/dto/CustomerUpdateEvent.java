package com.example.tpcm_spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateEvent {
    private String eventType; // CREATE, UPDATE_NAME, UPDATE_TYPE, UPDATE_BILL_CYCLE_DAY, DELETE
    private Long customerID;
    private String name;
    private String type;
    private Integer nrSubscribers;
    private Integer billCycleDay;
    private String email;
    private String contactNumber;
    private String address;

    private String changedField;
    private String oldValue;
    private String newValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String source = "CLIENTS_SERVICE";

    public static CustomerUpdateEvent createEvent(Long customerID, String name, String type, Integer billCycleDay, String email, String contactNumber, String address) {
        CustomerUpdateEvent event = new CustomerUpdateEvent();
        event.setEventType("CREATE");
        event.setCustomerID(customerID);
        event.setName(name);
        event.setType(type);
        event.setBillCycleDay(billCycleDay);
        event.setEmail(email);
        event.setContactNumber(contactNumber);
        event.setAddress(address);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static CustomerUpdateEvent nameUpdateEvent(Long customerID, String oldName, String newName) {
        CustomerUpdateEvent event = new CustomerUpdateEvent();
        event.setEventType("UPDATE_NAME");
        event.setCustomerID(customerID);
        event.setChangedField("name");
        event.setOldValue(oldName);
        event.setNewValue(newName);
        event.setName(newName);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static CustomerUpdateEvent typeUpdateEvent(Long customerID, String oldType, String newType) {
        CustomerUpdateEvent event = new CustomerUpdateEvent();
        event.setEventType("UPDATE_TYPE");
        event.setCustomerID(customerID);
        event.setChangedField("type");
        event.setOldValue(oldType);
        event.setNewValue(newType);
        event.setType(newType);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }

    public static CustomerUpdateEvent billCycleDayUpdateEvent(Long customerID, Integer oldDay, Integer newDay) {
        CustomerUpdateEvent event = new CustomerUpdateEvent();
        event.setEventType("UPDATE_BILL_CYCLE_DAY");
        event.setCustomerID(customerID);
        event.setChangedField("billCycleDay");
        event.setOldValue(oldDay != null ? oldDay.toString() : null);
        event.setNewValue(newDay != null ? newDay.toString() : null);
        event.setBillCycleDay(newDay);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }


    public static CustomerUpdateEvent deleteEvent(Long customerID) {
        CustomerUpdateEvent event = new CustomerUpdateEvent();
        event.setEventType("DELETE");
        event.setCustomerID(customerID);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}