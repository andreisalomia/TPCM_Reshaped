package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseCustomer;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseCustomerProcessor extends AbstractMessageProcessor<CloseCustomer> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCloseCustomer")
    @Timed("processCloseCustomer")
    public void process(CloseCustomer closeCustomer) {
        log.info("Processing CLOSE_CUSTOMER for customer ID: {}", closeCustomer.getCustomerID());

        tpcmApiService.deleteCustomer(closeCustomer.getCustomerID());

        log.info("Finished processing CLOSE_CUSTOMER for customer ID: {}", closeCustomer.getCustomerID());
    }
}