package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final Class<T> tClass;

    private final EventMapper eventMapper;

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            return eventMapper.readValue(bytes, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
