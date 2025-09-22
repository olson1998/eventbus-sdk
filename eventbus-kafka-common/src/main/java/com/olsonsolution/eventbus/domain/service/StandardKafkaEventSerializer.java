package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardKafkaEventSerializer<T> implements KafkaEventSerializer<T> {

    private final EventMapper eventMapper;

    @Override
    public byte[] serialize(String s, T t) {
        return eventMapper.writeValueAsBytes(t);
    }
}
