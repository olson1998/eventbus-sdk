package com.olsonsolution.eventbus.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

import java.io.IOException;

public class TypeReferenceEventMapper<T> extends AbstractEventMapper<T> {

    @NonNull
    private final TypeReference<T> typeReference;

    public TypeReferenceEventMapper(@NonNull String messageName,
                                    @NonNull ObjectMapper objectMapper,
                                    @NonNull TypeReference<T> typeReference) {
        super(messageName, objectMapper);
        this.typeReference = typeReference;
    }

    @Override
    protected T convertFromNode(JsonNode jsonNode) throws IOException {
        try {
            return getObjectMapper().convertValue(jsonNode, typeReference);
        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to convert to: " + typeReference, e);
        }
    }
}
