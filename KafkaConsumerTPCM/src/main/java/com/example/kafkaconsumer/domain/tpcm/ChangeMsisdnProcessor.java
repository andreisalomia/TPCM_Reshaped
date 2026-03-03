package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.ChangeMsisdn;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeMsisdnProcessor extends AbstractMessageProcessor<ChangeMsisdn> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processChangeMsisdn")
    @Timed("processChangeMsisdn")
    public void process(ChangeMsisdn changeMsisdn) {
        log.info("Processing CHANGE_MSISDN for subscriber ID: {}", changeMsisdn.getSubscriberID());

        tpcmApiService.updateSubscriber(
                changeMsisdn.getSubscriberID(),
                changeMsisdn.getMsisdn(),
                null,
                null,
                null
        );

        log.info("Finished processing CHANGE_MSISDN for subscriber ID: {}", changeMsisdn.getSubscriberID());
    }
}