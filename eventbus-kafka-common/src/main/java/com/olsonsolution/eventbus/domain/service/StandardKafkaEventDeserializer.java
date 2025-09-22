package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final Class<T> tClass;

    private final EventMapper eventMapper;

    @Override
    public T deserialize(String s, byte[] bytes) {
        return eventMapper.readValue(bytes, tClass);
    }
}
