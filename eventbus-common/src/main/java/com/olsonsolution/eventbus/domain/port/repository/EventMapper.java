package com.olsonsolution.eventbus.domain.port.repository;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface EventMapper<T> {

    String getMessageName();

    ObjectMapper getObjectMapper();

    byte[] writeValue(String channel, T object, AsyncAPI apiDocs) throws IOException;

    T readValue(String channel, byte[] bytes, AsyncAPI apiDocs) throws IOException;

}
