package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseCustomer;
import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import com.example.kafkaconsumer.repository.app.SubscriberRepositoryApp;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseCustomerProcessor extends AbstractMessageProcessor<CloseCustomer> {

    private final CustomerRepositoryApp customerRepository;
    private final SubscriberRepositoryApp subscriberRepository;

    @Override
    @Counted("processCloseCustomer")
    @Timed("processCloseCustomer")
    @Transactional
    public void process(CloseCustomer closeCustomer) {
        log.info("Processing CLOSE_CUSTOMER for customer ID: {}", closeCustomer.getCustomerID());

        try {
            Customer customer = customerRepository.findById(closeCustomer.getCustomerID()).orElse(null);

            if (customer == null) {
                log.warn("Customer with ID {} not found in app database, nothing to delete",
                        closeCustomer.getCustomerID());
                return;
            }

            customer.getSubscribers().forEach(subscriber -> {
                log.debug("Deleting subscriber {} for customer {}",
                        subscriber.getSubscriberID(), closeCustomer.getCustomerID());
                subscriberRepository.delete(subscriber);
            });

            customerRepository.delete(customer);
            log.info("Successfully deleted customer with ID {} from app database",
                    closeCustomer.getCustomerID());

        } catch (Exception e) {
            log.error("Error processing CLOSE_CUSTOMER for customer ID: {}",
                    closeCustomer.getCustomerID(), e);
            throw new RuntimeException("Failed to process CLOSE_CUSTOMER event", e);
        }
    }
}