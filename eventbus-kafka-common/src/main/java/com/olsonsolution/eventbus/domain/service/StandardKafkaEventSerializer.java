package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class StandardKafkaEventSerializer<T> implements KafkaEventSerializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    @SneakyThrows
    public byte[] serialize(String s, T t) {
        return eventMapper.writeValue(s, t, apiDocs);
    }

}
