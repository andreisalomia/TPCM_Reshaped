package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateCustomerName;
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
public class UpdateCustomerNameProcessor extends AbstractMessageProcessor<UpdateCustomerName> {

    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;

    @Override
    @Counted("processUpdateCustomerName")
    @Timed("processUpdateCustomerName")
    @Transactional
    public void process(UpdateCustomerName updateCustomerName) {
        log.info("Processing UPDATE_CUSTOMER_NAME for customer ID: {}", updateCustomerName.getCustomerID());

        try {
            Customer customer = customerRepositoryApp.findById(updateCustomerName.getCustomerID())
                    .orElseGet(() -> {
                        log.info("Customer {} not found in app database, importing from clients database",
                                updateCustomerName.getCustomerID());
                        return importCustomerFromClients(updateCustomerName.getCustomerID());
                    });

            if (customer == null) {
                log.error("Customer {} not found in either database", updateCustomerName.getCustomerID());
                return;
            }

            customer.setName(updateCustomerName.getName());
            customerRepositoryApp.save(customer);

            log.info("Successfully updated customer name for ID {} from '{}' to '{}'",
                    updateCustomerName.getCustomerID(),
                    updateCustomerName.getOldValue(),
                    updateCustomerName.getNewValue());

        } catch (Exception e) {
            log.error("Error processing UPDATE_CUSTOMER_NAME for customer ID: {}",
                    updateCustomerName.getCustomerID(), e);
            throw new RuntimeException("Failed to process UPDATE_CUSTOMER_NAME event", e);
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