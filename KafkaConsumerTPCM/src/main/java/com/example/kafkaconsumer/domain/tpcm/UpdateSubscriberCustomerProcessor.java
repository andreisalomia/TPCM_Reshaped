package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateSubscriberCustomer;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSubscriberCustomerProcessor extends AbstractMessageProcessor<UpdateSubscriberCustomer> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateSubscriberCustomer")
    @Timed("processUpdateSubscriberCustomer")
    public void process(UpdateSubscriberCustomer updateSubscriberCustomer) {
        log.info("Processing UPDATE_SUBSCRIBER_CUSTOMER for subscriber ID: {}", updateSubscriberCustomer.getSubscriberID());

        tpcmApiService.updateSubscriber(
                updateSubscriberCustomer.getSubscriberID(),
                null,
                null,
                null,
                updateSubscriberCustomer.getCustomerID()
        );

        log.info("Finished processing UPDATE_SUBSCRIBER_CUSTOMER for subscriber ID: {}", updateSubscriberCustomer.getSubscriberID());
    }
}