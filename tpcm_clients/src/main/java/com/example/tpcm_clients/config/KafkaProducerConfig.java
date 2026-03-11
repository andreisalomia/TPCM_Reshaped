package com.example.tpcm_clients.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private int retries;

    @Value("${kafka.topics.customer-updates}")
    private String customerUpdatesTopic;

    @Value("${kafka.topics.subscriber-updates}")
    private String subscriberUpdatesTopic;

    @Value("${kafka.topics.user-updates}")
    private String userUpdatesTopic;

    @Value("${kafka.topics.customer-updates.partitions}")
    private int customerPartitions;

    @Value("${kafka.topics.subscriber-updates.partitions}")
    private int subscriberPartitions;

    @Value("${kafka.topics.user-updates.partitions}")
    private int userPartitions;

    @Value("${kafka.topics.customer-updates.replication-factor}")
    private short customerReplicationFactor;

    @Value("${kafka.topics.subscriber-updates.replication-factor}")
    private short subscriberReplicationFactor;

    @Value("${kafka.topics.user-updates.replication-factor}")
    private short userReplicationFactor;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic customerUpdatesTopic() {
        return new NewTopic(customerUpdatesTopic, customerPartitions, customerReplicationFactor);
    }

    @Bean
    public NewTopic subscriberUpdatesTopic() {
        return new NewTopic(subscriberUpdatesTopic, subscriberPartitions, subscriberReplicationFactor);
    }

    @Bean
    public NewTopic userUpdatesTopic() {
        return new NewTopic(userUpdatesTopic, userPartitions, userReplicationFactor);
    }
}