package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    @SneakyThrows
    public T deserialize(String s, byte[] bytes) {
        return eventMapper.readValue(s, bytes, apiDocs);
    }
}
