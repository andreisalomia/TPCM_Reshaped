package com.example.kafkaconsumer.util;

import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.model.app.Limit;
import com.example.kafkaconsumer.model.app.Subscriber;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import com.example.kafkaconsumer.repository.clients.CustomerRepositoryClients;
import com.example.kafkaconsumer.repository.app.SubscriberRepositoryApp;
import com.example.kafkaconsumer.repository.app.LimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ImportProcessor {
    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;
    private final SubscriberRepositoryApp subscriberRepositoryApp;
    private final LimitRepository limitRepository;

    public Customer importCustomerFromClients(Long customerId) {
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

    public void updateCustomerSubscriberCount(Long customerId) {
        customerRepositoryApp.findById(customerId).ifPresent(customer -> {
            int count = subscriberRepositoryApp.countByCustomerCustomerID(customerId);
            customer.setNrSubscribers(count);
            customerRepositoryApp.save(customer);
        });
    }

    public void createDefaultLimit(Subscriber subscriber) {
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
