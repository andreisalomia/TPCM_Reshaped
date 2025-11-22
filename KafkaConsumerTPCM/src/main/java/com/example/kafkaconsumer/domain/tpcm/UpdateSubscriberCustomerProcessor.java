package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateSubscriberCustomer;
import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.model.app.Limit;
import com.example.kafkaconsumer.model.app.Subscriber;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import com.example.kafkaconsumer.repository.app.SubscriberRepositoryApp;
import com.example.kafkaconsumer.repository.clients.CustomerRepositoryClients;
import com.example.kafkaconsumer.repository.clients.SubscriberRepositoryClients;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kafkaconsumer.repository.app.LimitRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSubscriberCustomerProcessor extends AbstractMessageProcessor<UpdateSubscriberCustomer> {

    private final SubscriberRepositoryApp subscriberRepositoryApp;
    private final SubscriberRepositoryClients subscriberRepositoryClients;
    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;
    private final LimitRepository limitRepository;

    @Override
    @Counted("processUpdateSubscriberCustomer")
    @Timed("processUpdateSubscriberCustomer")
    @Transactional
    public void process(UpdateSubscriberCustomer updateCustomer) {
        log.info("Processing UPDATE_CUSTOMER for subscriber ID: {}", updateCustomer.getSubscriberID());

        try {
            Subscriber subscriber = subscriberRepositoryApp.findById(updateCustomer.getSubscriberID())
                    .orElseGet(() -> {
                        log.info("Subscriber {} not found in app database, importing from clients database",
                                updateCustomer.getSubscriberID());
                        return importSubscriberFromClients(updateCustomer.getSubscriberID());
                    });

            if (subscriber == null) {
                log.error("Subscriber {} not found in either database", updateCustomer.getSubscriberID());
                return;
            }

            Long oldCustomerId = subscriber.getCustomer().getCustomerID();

            Customer newCustomer = customerRepositoryApp.findById(updateCustomer.getCustomerID())
                    .orElseGet(() -> {
                        log.info("New customer {} not found in app database, importing from clients database",
                                updateCustomer.getCustomerID());
                        return importCustomerFromClients(updateCustomer.getCustomerID());
                    });

            if (newCustomer == null) {
                log.error("New customer {} not found in either database", updateCustomer.getCustomerID());
                return;
            }

            subscriber.setCustomer(newCustomer);
            subscriberRepositoryApp.save(subscriber);

            updateCustomerSubscriberCount(oldCustomerId);
            updateCustomerSubscriberCount(updateCustomer.getCustomerID());

            log.info("Successfully moved subscriber ID {} from customer {} to customer {}",
                    updateCustomer.getSubscriberID(),
                    oldCustomerId,
                    updateCustomer.getCustomerID());

        } catch (Exception e) {
            log.error("Error processing UPDATE_CUSTOMER for subscriber ID: {}",
                    updateCustomer.getSubscriberID(), e);
            throw new RuntimeException("Failed to process UPDATE_CUSTOMER event", e);
        }
    }

    private Subscriber importSubscriberFromClients(Long subscriberId) {
        return subscriberRepositoryClients.findById(subscriberId)
                .map(clientsSubscriber -> {
                    Customer customer = customerRepositoryApp.findById(clientsSubscriber.getCustomer().getCustomerID())
                            .orElseGet(() -> importCustomerFromClients(clientsSubscriber.getCustomer().getCustomerID()));

                    if (customer == null) {
                        log.error("Cannot import subscriber {} - customer {} not found",
                                subscriberId, clientsSubscriber.getCustomer().getCustomerID());
                        return null;
                    }

                    Subscriber appSubscriber = new Subscriber();
                    appSubscriber.setSubscriberID(clientsSubscriber.getSubscriberID());
                    appSubscriber.setMsisdn(clientsSubscriber.getMsisdn());
                    appSubscriber.setStatus(clientsSubscriber.getStatus());
                    appSubscriber.setSubscriptionType(clientsSubscriber.getSubscriptionType());
                    appSubscriber.setCustomer(customer);

                    Subscriber saved = subscriberRepositoryApp.save(appSubscriber);
                    createDefaultLimit(saved);
                    updateCustomerSubscriberCount(customer.getCustomerID());
                    return saved;
                })
                .orElse(null);
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