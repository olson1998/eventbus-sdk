package com.olsonsolution.eventbus.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.msgpack.jackson.dataformat.MessagePackFactory;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardEventMapper implements EventMapper {

    @Getter
    private final ObjectMapper mapper;

    public static StandardEventMapper create() {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);
        return new StandardEventMapper(objectMapper);
    }

    @Override
    public byte[] writeValueAsBytes(Object object) {
        return new byte[0];
    }

    @Override
    public <T> T readValue(byte[] bytes, Class<T> valueType) {
        return null;
    }

    @Override
    public <T> T readValue(String json, TypeReference<T> typeReference) {
        return null;
    }
}
