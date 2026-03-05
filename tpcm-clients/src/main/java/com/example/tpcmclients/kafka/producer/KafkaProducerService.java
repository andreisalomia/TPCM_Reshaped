package com.example.tpcm-clients.kafka.producer;

import com.example.tpcm_spring.dto.CustomerUpdateEvent;
import com.example.tpcm_spring.dto.SubscriberUpdateEvent;
import com.example.tpcm_spring.dto.AppUserUpdateEvent;
import com.example.tpcm_spring.models.clients.Customer;
import com.example.tpcm_spring.models.clients.Subscriber;
import com.example.tpcm_spring.models.clients.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.customer-updates}")
    private String customerUpdatesTopic;

    @Value("${kafka.topics.subscriber-updates}")
    private String subscriberUpdatesTopic;

    @Value("${kafka.topics.user-updates}")
    private String userUpdatesTopic;

    public void publishCustomerCreated(Customer customer) {
        CustomerUpdateEvent event = CustomerUpdateEvent.createEvent(
                customer.getCustomerID(), customer.getName(), customer.getType(),
                customer.getBillCycleDay(), customer.getEmail(),
                customer.getContactNumber(), customer.getAddress()
        );
        publishCustomerEvent(event, "createCustomer");
    }

    public void publishCustomerNameUpdate(Long customerId, String oldName, String newName) {
        CustomerUpdateEvent event = CustomerUpdateEvent.nameUpdateEvent(customerId, oldName, newName);
        publishCustomerEvent(event, "updateCustomerName");
    }

    public void publishCustomerTypeUpdate(Long customerId, String oldType, String newType) {
        CustomerUpdateEvent event = CustomerUpdateEvent.typeUpdateEvent(customerId, oldType, newType);
        publishCustomerEvent(event, "updateCustomerType");
    }

    public void publishCustomerBillCycleDayUpdate(Long customerId, Integer oldDay, Integer newDay) {
        CustomerUpdateEvent event = CustomerUpdateEvent.billCycleDayUpdateEvent(customerId, oldDay, newDay);
        publishCustomerEvent(event, "setBillDay");
    }

    public void publishCustomerDeleted(Long customerId) {
        CustomerUpdateEvent event = CustomerUpdateEvent.deleteEvent(customerId);
        publishCustomerEvent(event, "closeCustomer");
    }

    public void publishSubscriberCreated(Subscriber subscriber) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.createEvent(
                subscriber.getSubscriberID(), subscriber.getMsisdn(), subscriber.getStatus(),
                subscriber.getSubscriptionType(), subscriber.getCustomer().getCustomerID()
        );
        publishSubscriberEvent(event, "createSubscriber");
    }

    public void publishSubscriberMsisdnUpdate(Long subscriberId, String oldMsisdn, String newMsisdn) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.msisdnUpdateEvent(subscriberId, oldMsisdn, newMsisdn);
        publishSubscriberEvent(event, "changeMsisdn");
    }

    public void publishSubscriberStatusUpdate(Long subscriberId, String oldStatus, String newStatus) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.statusUpdateEvent(subscriberId, oldStatus, newStatus);
        publishSubscriberEvent(event, "updateSubscriberStatus");
    }

    public void publishSubscriberSubscriptionTypeUpdate(Long subscriberId, String oldType, String newType) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.subscriptionTypeUpdateEvent(subscriberId, oldType, newType);
        publishSubscriberEvent(event, "changeSubscriberType");
    }

    public void publishSubscriberCustomerUpdate(Long subscriberId, Long oldCustomerId, Long newCustomerId) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.customerUpdateEvent(subscriberId, oldCustomerId, newCustomerId);
        publishSubscriberEvent(event, "updateSubscriberCustomer");
    }

    public void publishSubscriberDeleted(Long subscriberId) {
        SubscriberUpdateEvent event = SubscriberUpdateEvent.deleteEvent(subscriberId);
        publishSubscriberEvent(event, "closeSubscriber");
    }

    public void publishUserCreated(AppUser user) {
        AppUserUpdateEvent event = new AppUserUpdateEvent();
        event.setEventType("CREATE");
        event.setUserID(user.getUserID());
        event.setUsername(user.getUsername());
        event.setRole(user.getRole());
        publishUserEvent(event, "createUser");
    }

    public void publishUserUpdated(AppUser user) {
        AppUserUpdateEvent event = new AppUserUpdateEvent();
        event.setEventType("UPDATE");
        event.setUserID(user.getUserID());
        event.setUsername(user.getUsername());
        event.setRole(user.getRole());
        publishUserEvent(event, "updateUser");
    }

    public void publishUserDeleted(Long userId) {
        AppUserUpdateEvent event = new AppUserUpdateEvent();
        event.setEventType("DELETE");
        event.setUserID(userId);
        publishUserEvent(event, "closeUser");
    }

    private void publishCustomerEvent(CustomerUpdateEvent event, String operationType) {
        String key = "customer-" + event.getCustomerID();

        Message<CustomerUpdateEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, customerUpdatesTopic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("operationType", operationType)
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send customer event: {}", event, ex);
            } else {
                log.info("Customer event sent successfully: {} for customer {} to partition: {}",
                        event.getEventType(), event.getCustomerID(), result.getRecordMetadata().partition());
            }
        });
    }

    private void publishSubscriberEvent(SubscriberUpdateEvent event, String operationType) {
        String key = "subscriber-" + event.getSubscriberID();

        Message<SubscriberUpdateEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, subscriberUpdatesTopic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("operationType", operationType)
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send subscriber event: {}", event, ex);
            } else {
                log.info("Subscriber event sent successfully: {} for subscriber {} to partition: {}",
                        event.getEventType(), event.getSubscriberID(), result.getRecordMetadata().partition());
            }
        });
    }

    private void publishUserEvent(AppUserUpdateEvent event, String operationType) {
        String key = "user-" + event.getUserID();

        Message<AppUserUpdateEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, userUpdatesTopic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("operationType", operationType)
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send user event: {}", event, ex);
            } else {
                log.info("User event sent successfully: {} for user {} to partition: {}",
                        event.getEventType(), event.getUserID(), result.getRecordMetadata().partition());
            }
        });
    }
}