package com.example.kafkaconsumer.entity;

import lombok.Getter;

@Getter
public enum SupportedOperationType {
    createCustomer(CreateCustomer.class),
    updateCustomerName(UpdateCustomerName.class),
    updateCustomerType(UpdateCustomerType.class),
    setBillDay(SetBillDay.class),
    updateCustomerEmail(UpdateCustomerEmail.class),
    closeCustomer(CloseCustomer.class),

    createSubscriber(CreateSubscriber.class),
    changeMsisdn(ChangeMsisdn.class),
    changeSubscriberType(ChangeSubscriberType.class),
    closeSubscriber(CloseSubscriber.class),
    updateSubscriberStatus(UpdateSubscriberStatus.class),
    updateSubscriberCustomer(UpdateSubscriberCustomer.class),

    createUser(CreateUser.class),
    updateUser(UpdateUser.class),
    closeUser(CloseUser.class);

    private final Class<? extends BaseTopicEntry> entry;

    SupportedOperationType(Class<? extends BaseTopicEntry> entry) {
        this.entry = entry;
    }
}