package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.model.StandardMappingResult;
import com.olsonsolution.eventbus.domain.model.kafka.exception.KafkaDeserializationException;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    public MappingResult<T> deserialize(String s, byte[] bytes) {
        try {
            T content = eventMapper.readValue(s, bytes, apiDocs);
            return new StandardMappingResult<>(content, null);
        } catch (IOException e) {
            KafkaDeserializationException exception = new KafkaDeserializationException(e);
            return new StandardMappingResult<>(null, exception);
        }
    }

}
