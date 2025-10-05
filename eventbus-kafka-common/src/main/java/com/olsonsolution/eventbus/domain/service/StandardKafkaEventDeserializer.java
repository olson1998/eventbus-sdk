package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.model.StandardCorruptedEventMessage;
import com.olsonsolution.eventbus.domain.model.StandardEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventDeserializer;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class StandardKafkaEventDeserializer<T> implements KafkaEventDeserializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    public EventMessage<T> deserialize(String s, byte[] bytes) {
        try {
            T content = eventMapper.readValue(s, bytes, apiDocs);
            return StandardEventMessage.<T>eventMessageBuilder()
                    .content(content)
                    .build();
        } catch (IOException e) {
            return StandardCorruptedEventMessage.<T>corruptedEventBuilder()
                    .corruptionCause(e)
                    .build();
        }
    }

}
