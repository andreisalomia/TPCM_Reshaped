package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.ChangeSubscriberType;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeSubscriberTypeProcessor extends AbstractMessageProcessor<ChangeSubscriberType> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processChangeSubscriberType")
    @Timed("processChangeSubscriberType")
    public void process(ChangeSubscriberType changeSubscriberType) {
        log.info("Processing CHANGE_SUBSCRIBER_TYPE for subscriber ID: {}", changeSubscriberType.getSubscriberID());

        tpcmApiService.updateSubscriber(
                changeSubscriberType.getSubscriberID(),
                null,
                null,
                changeSubscriberType.getSubscriptionType(),
                null
        );

        log.info("Successfully processed CHANGE_SUBSCRIBER_TYPE for subscriber ID: {}", changeSubscriberType.getSubscriberID());
    }
}