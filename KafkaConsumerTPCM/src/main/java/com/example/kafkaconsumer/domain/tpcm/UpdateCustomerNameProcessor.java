package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateCustomerName;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerNameProcessor extends AbstractMessageProcessor<UpdateCustomerName> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateCustomerName")
    @Timed("processUpdateCustomerName")
    public void process(UpdateCustomerName updateCustomerName) {
        log.info("Processing UPDATE_CUSTOMER_NAME for customer ID: {}", updateCustomerName.getCustomerID());

        tpcmApiService.updateCustomer(
                updateCustomerName.getCustomerID(),
                updateCustomerName.getName(),
                null,
                null,
                null
        );

        log.info("Finished processing UPDATE_CUSTOMER_NAME for customer ID: {}", updateCustomerName.getCustomerID());
    }
}