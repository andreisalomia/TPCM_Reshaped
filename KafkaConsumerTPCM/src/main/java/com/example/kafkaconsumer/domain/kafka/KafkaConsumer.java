package com.example.kafkaconsumer.domain.kafka;

import com.example.kafkaconsumer.domain.tpcm.AbstractMessageProcessor;
import com.example.kafkaconsumer.domain.tpcm.MessageProcessorFactory;
import com.example.kafkaconsumer.entity.SupportedOperationType;
import com.example.kafkaconsumer.entity.BaseTopicEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final MessageProcessorFactory messageProcessorFactory;
    private final ObjectMapper mapper = new ObjectMapper();

    @Counted("consumeAll")
    @Timed("consumeAll")
    @KafkaListener(
            id = "tpcmKafkaConsumerContainer",
            topics = {"customer-updates", "subscriber-updates", "user-updates"},
            containerFactory = "myKafkaListenerContainerFactory"
    )
    public <T extends BaseTopicEntry> void consumeAll(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Headers MessageHeaders headers) {

        String operationType = new String(Objects.requireNonNull(headers.get("operationType", String.class)));
        log.info("Consumed message from topic {}, of type {} -> {}", topic, operationType, message);

        if (EnumUtils.isValidEnum(SupportedOperationType.class, operationType)) {
            SupportedOperationType s = SupportedOperationType.valueOf(operationType);
            log.info("Found operation type {}", s.getEntry().getName());
            try {
                @SuppressWarnings("unchecked")  
                Class<T> entryClass = (Class<T>) s.getEntry();
                T entry = mapper.readValue(message, entryClass);

                AbstractMessageProcessor<T> processor = messageProcessorFactory.getMessageProcessor(entryClass);
                processor.process(entry);
            } catch (Exception e) {
                log.error("Message processor exception ", e);
            }
        } else {
            log.error("Unsupported operation type {}", operationType);
        }
    }
}