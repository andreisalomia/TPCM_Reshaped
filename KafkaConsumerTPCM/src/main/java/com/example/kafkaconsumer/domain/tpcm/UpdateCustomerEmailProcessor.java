package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateCustomerEmail;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerEmailProcessor extends AbstractMessageProcessor<UpdateCustomerEmail> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateCustomerEmail")
    @Timed("processUpdateCustomerEmail")
    public void process(UpdateCustomerEmail updateCustomerEmail) {
        log.info("Processing UPDATE_EMAIL for customer ID: {}", updateCustomerEmail.getCustomerID());

        tpcmApiService.updateCustomerEmail(
                updateCustomerEmail.getCustomerID(),
                updateCustomerEmail.getEmail()
        );

        log.info("Finished processing UPDATE_EMAIL for customer ID: {}", updateCustomerEmail.getCustomerID());
    }
}