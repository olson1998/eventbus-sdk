package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventSerializer<T> implements KafkaEventSerializer<T> {

    private final EventMapper eventMapper;

    @Override
    public byte[] serialize(String s, T t) {
        try {
            return eventMapper.writeValueAsBytes(t);
        } catch (IOException e) {
            throw new KafkaException(e);
        }
    }
}
