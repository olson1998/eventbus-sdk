package com.olsonsolution.eventbus.domain.service;

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
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory)
                .configure(WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
        return new StandardEventMapper(objectMapper);
    }

    @Override
    public byte[] writeValueAsBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    @Override
    public <T> T readValue(byte[] bytes, Class<T> valueType) throws IOException {
        return mapper.readValue(bytes, valueType);
    }

    @Override
    public <T> T readValue(byte[] bytes, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(bytes, typeReference);
    }
}
