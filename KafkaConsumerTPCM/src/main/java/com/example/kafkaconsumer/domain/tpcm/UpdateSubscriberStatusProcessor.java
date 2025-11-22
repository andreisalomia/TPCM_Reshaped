package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateSubscriberStatus;
import com.example.kafkaconsumer.model.app.Customer;
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
import com.example.kafkaconsumer.model.app.Limit;
import com.example.kafkaconsumer.util.ImportProcessor;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSubscriberStatusProcessor extends AbstractMessageProcessor<UpdateSubscriberStatus> {

    private final SubscriberRepositoryApp subscriberRepositoryApp;
    private final SubscriberRepositoryClients subscriberRepositoryClients;
    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;
    private final LimitRepository limitRepository;

    @Override
    @Counted("processUpdateSubscriberStatus")
    @Timed("processUpdateSubscriberStatus")
    @Transactional
    public void process(UpdateSubscriberStatus updateStatus) {
        log.info("Processing UPDATE_STATUS for subscriber ID: {}", updateStatus.getSubscriberID());

        try {
            Subscriber subscriber = subscriberRepositoryApp.findById(updateStatus.getSubscriberID())
                    .orElseGet(() -> {
                        log.info("Subscriber {} not found in app database, importing from clients database",
                                updateStatus.getSubscriberID());
                        return importSubscriberFromClients(updateStatus.getSubscriberID());
                    });

            if (subscriber == null) {
                log.error("Subscriber {} not found in either database", updateStatus.getSubscriberID());
                return;
            }

            subscriber.setStatus(updateStatus.getStatus());
            subscriberRepositoryApp.save(subscriber);

            log.info("Successfully updated status for subscriber ID {} from '{}' to '{}'",
                    updateStatus.getSubscriberID(),
                    updateStatus.getOldValue(),
                    updateStatus.getNewValue());

        } catch (Exception e) {
            log.error("Error processing UPDATE_STATUS for subscriber ID: {}",
                    updateStatus.getSubscriberID(), e);
            throw new RuntimeException("Failed to process UPDATE_STATUS event", e);
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
                    if (customerRepositoryApp.existsById(customerId)) {
                        return customerRepositoryApp.findById(customerId).orElse(null);
                    }
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