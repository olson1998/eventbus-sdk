package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.schemas.asyncapi.AsyncAPISchema;
import com.asyncapi.schemas.asyncapi.Reference;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.message.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
public class StandardKafkaEventSerializer<T> implements KafkaEventSerializer<T> {

    private final AsyncAPI apiDocs;

    private final EventMapper<T> eventMapper;

    @Override
    @SneakyThrows
    public byte[] serialize(String s, T t) {
        return eventMapper.writeValue(s, t, apiDocs);
    }

    private JsonNode mapToEventContent(Message message, T t) {
        JsonNode content = objectMapper.convertValue(t, JsonNode.class);
        Object payload = message.getPayload();
        AsyncAPISchema dtoSchema = null;
        if (payload instanceof AsyncAPISchema apiSchema) {
            dtoSchema = apiSchema;
        } else if (payload instanceof Reference reference) {
            dtoSchema = Optional.ofNullable(apiDocs.getComponents())
                    .flatMap(components -> AsyncAPIUtils.findApiSchema(components, reference.getRef()))
                    .orElse(null);
        }
        return mapByDtoSchema(content, dtoSchema);
    }

    private JsonNode mapByDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {
        if (StringUtils.equals(dtoSchema.getRef(), "string")) {
            return mapByStringDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "number")) {
            return mapByNumberDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "integer")) {
            return mapByIntegerDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "number")) {
            return mapByNumberDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "boolean")) {
            return mapByBooleanDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "object")) {
            return mapByObjectDtoSchema(content, dtoSchema);
        } else if (StringUtils.equals(dtoSchema.getRef(), "array")) {
            return mapByArrayDtoSchema(content, dtoSchema);
        } else {
            return NullNode.getInstance();
        }
    }

    private TextNode mapByStringDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {
        if (content instanceof TextNode textContent) {
            return textContent;
        } else {
            return NullNode.getInstance();
        }
    }

    private NumericNode mapByNumberDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {
        if (content instanceof TextNode textContent) {
            return textContent;
        } else {
            return NullNode.getInstance();
        }
    }

    private IntNode mapByIntegerDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {

    }

    private BooleanNode mapByBooleanDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {

    }

    private ObjectNode mapByObjectDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {

    }

    private ArrayNode mapByArrayDtoSchema(JsonNode content, AsyncAPISchema dtoSchema) {

    }

}
