package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateSubscriber;
import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.model.app.Limit;
import com.example.kafkaconsumer.model.app.Subscriber;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import com.example.kafkaconsumer.repository.app.LimitRepository;
import com.example.kafkaconsumer.repository.app.SubscriberRepositoryApp;
import com.example.kafkaconsumer.repository.clients.CustomerRepositoryClients;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSubscriberProcessor extends AbstractMessageProcessor<CreateSubscriber> {

    private final SubscriberRepositoryApp subscriberRepositoryApp;
    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;
    private final LimitRepository limitRepository;

    @Override
    @Counted("processCreateSubscriber")
    @Timed("processCreateSubscriber")
    @Transactional
    public void process(CreateSubscriber createSubscriber) {
        log.info("Processing CREATE_SUBSCRIBER for subscriber ID: {}", createSubscriber.getSubscriberID());

        try {
            if (subscriberRepositoryApp.existsById(createSubscriber.getSubscriberID())) {
                log.warn("Subscriber with ID {} already exists in app database", createSubscriber.getSubscriberID());
                return;
            }

            Customer customer = customerRepositoryApp.findById(createSubscriber.getCustomerID())
                    .orElseGet(() -> {
                        log.info("Customer {} not found in app database, importing from clients database",
                                createSubscriber.getCustomerID());
                        return importCustomerFromClients(createSubscriber.getCustomerID());
                    });

            if (customer == null) {
                log.error("Customer {} not found in either database", createSubscriber.getCustomerID());
                return;
            }

            Subscriber subscriber = new Subscriber();
            subscriber.setSubscriberID(createSubscriber.getSubscriberID());
            subscriber.setMsisdn(createSubscriber.getMsisdn());
            subscriber.setStatus(createSubscriber.getStatus());
            subscriber.setSubscriptionType(createSubscriber.getSubscriptionType());
            subscriber.setCustomer(customer);

            Subscriber saved = subscriberRepositoryApp.save(subscriber);

            createDefaultLimit(saved);

            updateCustomerSubscriberCount(customer.getCustomerID());

            log.info("Successfully created subscriber with ID {} in app database", createSubscriber.getSubscriberID());

        } catch (Exception e) {
            log.error("Error processing CREATE_SUBSCRIBER for subscriber ID: {}", createSubscriber.getSubscriberID(), e);
            throw new RuntimeException("Failed to process CREATE_SUBSCRIBER event", e);
        }
    }

    private Customer importCustomerFromClients(Long customerId) {
        return customerRepositoryClients.findById(customerId)
                .map(clientsCustomer -> {
                    Customer appCustomer = new Customer();
                    appCustomer.setCustomerID(clientsCustomer.getCustomerID());
                    appCustomer.setName(clientsCustomer.getName());
                    appCustomer.setType(clientsCustomer.getType());
                    appCustomer.setBillCycleDay(clientsCustomer.getBillCycleDay());
                    appCustomer.setNrSubscribers(clientsCustomer.getNrSubscribers() != null ?
                            clientsCustomer.getNrSubscribers() : 0);

                    return customerRepositoryApp.save(appCustomer);
                })
                .orElse(null);
    }

    private void updateCustomerSubscriberCount(Long customerId) {
        customerRepositoryApp.findById(customerId).ifPresent(customer -> {
            int count = subscriberRepositoryApp.countByCustomerCustomerID(customerId);
            customer.setNrSubscribers(count);
            customerRepositoryApp.save(customer);
            log.debug("Updated subscriber count for customer {} to {}", customerId, count);
        });
    }

    private void createDefaultLimit(Subscriber subscriber) {
        if (limitRepository.findBySubscriberID(subscriber.getSubscriberID()) == null) {
            Limit limit = new Limit();
            limit.setSubscriber(subscriber);
            limit.setConsumedAmount(0.0);
            limit.setMaxAmountCycle(300);
            limit.setMaxAmountTransaction(50);
            limit.setLastReset(new java.sql.Timestamp(System.currentTimeMillis()));

            limitRepository.save(limit);
            log.info("Created default limit for subscriber ID: {}", subscriber.getSubscriberID());
        }
    }
}