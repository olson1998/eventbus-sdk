package com.olsonsolution.eventbus.domain.port.repository;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface EventMapper {

    ObjectMapper getMapper();

    byte[] writeValueAsBytes(Object object) throws IOException;

    <T> T readValue(byte[] bytes, Class<T> valueType, AsyncAPI apiDocs) throws IOException;

    <T> T readValue(byte[] bytes, TypeReference<T> typeReference, AsyncAPI apiDocs) throws IOException;

}
