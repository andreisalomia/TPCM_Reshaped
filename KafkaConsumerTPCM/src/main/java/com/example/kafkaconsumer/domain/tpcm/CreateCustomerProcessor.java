package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateCustomer;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCustomerProcessor extends AbstractMessageProcessor<CreateCustomer> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCreateCustomer")
    @Timed("processCreateCustomer")
    public void process(CreateCustomer createCustomer) {
        log.info("Processing CREATE_CUSTOMER for customer ID: {}", createCustomer.getCustomerID());

        tpcmApiService.createCustomer(
                createCustomer.getCustomerID(),
                createCustomer.getName(),
                createCustomer.getType(),
                createCustomer.getBillCycleDay()
        );

        log.info("Successfully processed CREATE_CUSTOMER for customer ID: {}", createCustomer.getCustomerID());
    }
}