package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateCustomer;
import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCustomerProcessor extends AbstractMessageProcessor<CreateCustomer> {

    private final CustomerRepositoryApp customerRepositoryApp;

    @Override
    @Counted("processCreateCustomer")
    @Timed("processCreateCustomer")
    @Transactional
    public void process(CreateCustomer createCustomer) {
        log.info("Processing CREATE_CUSTOMER for customer ID: {}", createCustomer.getCustomerID());

        try {
            if (customerRepositoryApp.existsById(createCustomer.getCustomerID())) {
                log.warn("Customer with ID {} already exists in app database", createCustomer.getCustomerID());
                return;
            }

            Customer customer = new Customer();
            customer.setCustomerID(createCustomer.getCustomerID());
            customer.setName(createCustomer.getName());
            customer.setType(createCustomer.getType());
            customer.setBillCycleDay(createCustomer.getBillCycleDay());
            customer.setNrSubscribers(0);

            customerRepositoryApp.save(customer);
            log.info("Successfully created customer with ID {} in app database", createCustomer.getCustomerID());

        } catch (Exception e) {
            log.error("Error processing CREATE_CUSTOMER for customer ID: {}", createCustomer.getCustomerID(), e);
            throw new RuntimeException("Failed to process CREATE_CUSTOMER event", e);
        }
    }
}