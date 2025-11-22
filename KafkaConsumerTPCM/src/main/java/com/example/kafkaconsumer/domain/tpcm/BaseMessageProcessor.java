package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.BaseTopicEntry;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseMessageProcessor extends AbstractMessageProcessor<BaseTopicEntry> {

    @Override
    @Counted("processBaseMessage")
    @Timed("processBaseMessage")
    public void process(BaseTopicEntry baseTopicEntry) {
        log.info("BaseTopicEntry Process");
//        tpcmDAO.refresh(baseTopicEntry.getKeyValue()); wtf is this
    }
}