package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.model.kafka.exception.KafkaDeserializationException;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            return eventMapper.readValue(s, bytes, apiDocs);
        } catch (IOException e) {
            throw new KafkaDeserializationException(e);
        }
    }
}
