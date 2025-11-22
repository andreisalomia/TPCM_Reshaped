package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.SetBillDay;
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
public class SetBillDayProcessor extends AbstractMessageProcessor<SetBillDay> {

    private final CustomerRepositoryApp customerRepositoryApp;
    private final CustomerRepositoryClients customerRepositoryClients;

    @Override
    @Counted("processSetBillDay")
    @Timed("processSetBillDay")
    @Transactional
    public void process(SetBillDay setBillDay) {
        log.info("Processing SET_BILL_DAY for customer ID: {}", setBillDay.getCustomerID());

        try {
            Customer customer = customerRepositoryApp.findById(setBillDay.getCustomerID())
                    .orElseGet(() -> {
                        log.info("Customer {} not found in app database, importing from clients database",
                                setBillDay.getCustomerID());
                        return importCustomerFromClients(setBillDay.getCustomerID());
                    });

            if (customer == null) {
                log.error("Customer {} not found in either database", setBillDay.getCustomerID());
                return;
            }

            customer.setBillCycleDay(setBillDay.getBillCycleDay());
            customerRepositoryApp.save(customer);

            log.info("Successfully updated bill cycle day for customer ID {} from '{}' to '{}'",
                    setBillDay.getCustomerID(),
                    setBillDay.getOldValue(),
                    setBillDay.getNewValue());

        } catch (Exception e) {
            log.error("Error processing SET_BILL_DAY for customer ID: {}",
                    setBillDay.getCustomerID(), e);
            throw new RuntimeException("Failed to process SET_BILL_DAY event", e);
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