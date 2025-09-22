package com.olsonsolution.eventbus.domain.port.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface EventMapper {

    ObjectMapper getMapper();

    byte[] writeValueAsBytes(Object object);

    <T> T readValue(byte[] bytes, Class<T> valueType);

    <T> T readValue(String json, TypeReference<T> typeReference);

}
