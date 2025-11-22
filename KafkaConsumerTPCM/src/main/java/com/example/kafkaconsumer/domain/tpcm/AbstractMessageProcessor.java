package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.BaseTopicEntry;

public abstract class AbstractMessageProcessor<T extends BaseTopicEntry> {
    public abstract void process(T message);
}