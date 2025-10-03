package com.olsonsolution.eventbus.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.io.IOException;

public class ClassEventMapper<T> extends AbstractEventMapper<T> {

    @NonNull
    private final Class<T> javaClass;

    public ClassEventMapper(@NonNull String messageName,
                            @NonNull ObjectMapper objectMapper,
                            @NonNull Class<T> javaClass) {
        super(messageName, objectMapper);
        this.javaClass = javaClass;
    }

    @Override
    protected T convertFromNode(JsonNode jsonNode) throws IOException {
        try {
            return getObjectMapper().convertValue(jsonNode, javaClass);
        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to convert to: " + javaClass, e);
        }
    }

}
