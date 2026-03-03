package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateCustomerType;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerTypeProcessor extends AbstractMessageProcessor<UpdateCustomerType> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateCustomerType")
    @Timed("processUpdateCustomerType")
    public void process(UpdateCustomerType updateCustomerType) {
        log.info("Processing UPDATE_CUSTOMER_TYPE for customer ID: {}", updateCustomerType.getCustomerID());

        tpcmApiService.updateCustomer(
                updateCustomerType.getCustomerID(),
                null,
                updateCustomerType.getType(),
                null,
                null
        );

        log.info("Finished processing UPDATE_CUSTOMER_TYPE for customer ID: {}", updateCustomerType.getCustomerID());
    }
}