package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractEventMapper<T> implements EventMapper<T> {

    @Getter
    private final String messageName;
    @Getter
    @NonNull
    private final ObjectMapper objectMapper;

    @Override
    public byte[] writeValue(String channel, T object, AsyncAPI apiDocs) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public T readValue(String channel, byte[] bytes, AsyncAPI apiDocs) throws IOException {
        return convertFromNode(objectMapper.readTree(bytes));
    }

    protected abstract T convertFromNode(JsonNode jsonNode) throws IOException;

}
