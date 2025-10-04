package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.model.kafka.exception.KafkaSerializationException;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventSerializer<T> implements KafkaEventSerializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    public byte[] serialize(String s, T t) {
        try {
            return eventMapper.writeValue(s, t, apiDocs);
        } catch (IOException e) {
            throw new KafkaSerializationException(e);
        }
    }

}
