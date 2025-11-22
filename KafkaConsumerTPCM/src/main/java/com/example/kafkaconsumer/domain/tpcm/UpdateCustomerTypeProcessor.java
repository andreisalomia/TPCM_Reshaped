package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateCustomerType;
import com.example.kafkaconsumer.model.app.Customer;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
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
public class UpdateCustomerTypeProcessor extends AbstractMessageProcessor<UpdateCustomerType> {

    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;

    @Override
    @Counted("processUpdateCustomerType")
    @Timed("processUpdateCustomerType")
    @Transactional
    public void process(UpdateCustomerType updateCustomerType) {
        log.info("Processing UPDATE_CUSTOMER_TYPE for customer ID: {}", updateCustomerType.getCustomerID());

        try {
            Customer customer = customerRepositoryApp.findById(updateCustomerType.getCustomerID())
                    .orElseGet(() -> {
                        log.info("Customer {} not found in app database, importing from clients database",
                                updateCustomerType.getCustomerID());
                        return importCustomerFromClients(updateCustomerType.getCustomerID());
                    });

            if (customer == null) {
                log.error("Customer {} not found in either database", updateCustomerType.getCustomerID());
                return;
            }

            customer.setType(updateCustomerType.getType());
            customerRepositoryApp.save(customer);

            log.info("Successfully updated customer type for ID {} from '{}' to '{}'",
                    updateCustomerType.getCustomerID(),
                    updateCustomerType.getOldValue(),
                    updateCustomerType.getNewValue());

        } catch (Exception e) {
            log.error("Error processing UPDATE_CUSTOMER_TYPE for customer ID: {}",
                    updateCustomerType.getCustomerID(), e);
            throw new RuntimeException("Failed to process UPDATE_CUSTOMER_TYPE event", e);
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
}