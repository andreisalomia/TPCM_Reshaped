package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProcessorFactory {

    private final BaseMessageProcessor baseMessageProcessor;
    private final CreateCustomerProcessor createCustomerProcessor;
    private final UpdateCustomerNameProcessor updateCustomerNameProcessor;
    private final UpdateCustomerTypeProcessor updateCustomerTypeProcessor;
    private final SetBillDayProcessor setBillDayProcessor;
    private final CloseCustomerProcessor closeCustomerProcessor;
    private final CreateSubscriberProcessor createSubscriberProcessor;
    private final ChangeMsisdnProcessor changeMsisdnProcessor;
    private final ChangeSubscriberTypeProcessor changeSubscriberTypeProcessor;
    private final CloseSubscriberProcessor closeSubscriberProcessor;
    private final CreateUserProcessor createUserProcessor;
    private final UpdateUserProcessor updateUserProcessor;
    private final CloseUserProcessor closeUserProcessor;
    private final UpdateSubscriberStatusProcessor updateSubscriberStatusProcessor;
    private final UpdateSubscriberCustomerProcessor updateSubscriberCustomerProcessor;
    private final UpdateCustomerEmailProcessor updateCustomerEmailProcessor;

    @SuppressWarnings("unchecked")
    public <T extends BaseTopicEntry> AbstractMessageProcessor<T> getMessageProcessor(Class<T> entry) {
        if (entry.equals(CreateCustomer.class)) return (AbstractMessageProcessor<T>) createCustomerProcessor;
        if (entry.equals(UpdateCustomerName.class)) return (AbstractMessageProcessor<T>) updateCustomerNameProcessor;
        if (entry.equals(UpdateCustomerType.class)) return (AbstractMessageProcessor<T>) updateCustomerTypeProcessor;
        if (entry.equals(SetBillDay.class)) return (AbstractMessageProcessor<T>) setBillDayProcessor;
        if (entry.equals(CloseCustomer.class)) return (AbstractMessageProcessor<T>) closeCustomerProcessor;
        if (entry.equals(CreateSubscriber.class)) return (AbstractMessageProcessor<T>) createSubscriberProcessor;
        if (entry.equals(ChangeMsisdn.class)) return (AbstractMessageProcessor<T>) changeMsisdnProcessor;
        if (entry.equals(ChangeSubscriberType.class)) return (AbstractMessageProcessor<T>) changeSubscriberTypeProcessor;
        if (entry.equals(CloseSubscriber.class)) return (AbstractMessageProcessor<T>) closeSubscriberProcessor;
        if (entry.equals(CreateUser.class)) return (AbstractMessageProcessor<T>) createUserProcessor;
        if (entry.equals(UpdateUser.class)) return (AbstractMessageProcessor<T>) updateUserProcessor;
        if (entry.equals(CloseUser.class)) return (AbstractMessageProcessor<T>) closeUserProcessor;
        if (entry.equals(UpdateSubscriberStatus.class)) return (AbstractMessageProcessor<T>) updateSubscriberStatusProcessor;
        if (entry.equals(UpdateSubscriberCustomer.class)) return (AbstractMessageProcessor<T>) updateSubscriberCustomerProcessor;
        if (entry.equals(UpdateCustomerEmail.class)) return (AbstractMessageProcessor<T>) updateCustomerEmailProcessor;
        return (AbstractMessageProcessor<T>) baseMessageProcessor;
    }
}