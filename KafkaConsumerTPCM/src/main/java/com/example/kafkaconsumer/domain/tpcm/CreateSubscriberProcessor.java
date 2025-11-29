package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateSubscriber;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSubscriberProcessor extends AbstractMessageProcessor<CreateSubscriber> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCreateSubscriber")
    @Timed("processCreateSubscriber")
    public void process(CreateSubscriber createSubscriber) {
        log.info("Processing CREATE_SUBSCRIBER for subscriber ID: {}", createSubscriber.getSubscriberID());

        tpcmApiService.createSubscriber(
                createSubscriber.getSubscriberID(),
                createSubscriber.getMsisdn(),
                createSubscriber.getStatus(),
                createSubscriber.getSubscriptionType(),
                createSubscriber.getCustomerID()
        );

        log.info("Successfully processed CREATE_SUBSCRIBER for subscriber ID: {}", createSubscriber.getSubscriberID());
    }
}