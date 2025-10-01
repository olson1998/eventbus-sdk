package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardEventMapper implements EventMapper {

    @Getter
    private final ObjectMapper mapper;

    public static StandardEventMapper create() {
        MessagePackFactory factory = new MessagePackFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return new StandardEventMapper(mapper);
    }

    @Override
    public byte[] writeValueAsBytes(Object object) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T readValue(byte[] bytes, Class<T> valueType, AsyncAPI apiDocs) throws IOException {
        return null;
    }

    @Override
    public <T> T readValue(byte[] bytes, TypeReference<T> typeReference, AsyncAPI apiDocs) throws IOException {
        return null;
    }

}
