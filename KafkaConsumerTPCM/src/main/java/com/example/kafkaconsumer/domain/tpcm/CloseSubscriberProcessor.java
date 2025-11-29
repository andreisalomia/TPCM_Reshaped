package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseSubscriber;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseSubscriberProcessor extends AbstractMessageProcessor<CloseSubscriber> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCloseSubscriber")
    @Timed("processCloseSubscriber")
    public void process(CloseSubscriber closeSubscriber) {
        log.info("Processing CLOSE_SUBSCRIBER for subscriber ID: {}", closeSubscriber.getSubscriberID());

        tpcmApiService.deleteSubscriber(closeSubscriber.getSubscriberID());

        log.info("Successfully processed CLOSE_SUBSCRIBER for subscriber ID: {}", closeSubscriber.getSubscriberID());
    }
}