package com.olsonsolution.eventbus.domain.port.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface EventMapper {

    ObjectMapper getMapper();

    byte[] writeValueAsBytes(Object object) throws IOException;

    <T> T readValue(byte[] bytes, Class<T> valueType) throws IOException;

    <T> T readValue(byte[] bytes, TypeReference<T> typeReference) throws IOException;

}
