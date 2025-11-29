package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateSubscriberStatus;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSubscriberStatusProcessor extends AbstractMessageProcessor<UpdateSubscriberStatus> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateSubscriberStatus")
    @Timed("processUpdateSubscriberStatus")
    public void process(UpdateSubscriberStatus updateSubscriberStatus) {
        log.info("Processing UPDATE_SUBSCRIBER_STATUS for subscriber ID: {}", updateSubscriberStatus.getSubscriberID());

        tpcmApiService.updateSubscriber(
                updateSubscriberStatus.getSubscriberID(),
                null,
                updateSubscriberStatus.getStatus(),
                null,
                null
        );

        log.info("Successfully processed UPDATE_SUBSCRIBER_STATUS for subscriber ID: {}", updateSubscriberStatus.getSubscriberID());
    }
}