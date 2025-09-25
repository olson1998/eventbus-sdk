package com.olsonsolution.eventbus.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.olsonsolution.eventbus.domain.model.SampleContent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StandardEventMapperTest {

    private static final StandardEventMapper STANDARD_EVENT_MAPPER = StandardEventMapper.create();
    private static final SampleContent SAMPLE_CONTENT = new SampleContent(
            "John",
            "Doe",
            LocalDate.of(1990, 1, 1),
            1,
            ZonedDateTime.now(),
            List.of("Johny", "Doughman")
    );

    @Test
    void shouldInstantiateStandardEventMapperUsingCreateMethod() throws IOException {
        byte[] binaryEvent = STANDARD_EVENT_MAPPER.writeValueAsBytes(SAMPLE_CONTENT);
        System.out.println(new String(binaryEvent));
        JsonNode node = STANDARD_EVENT_MAPPER.readValue(binaryEvent, JsonNode.class);
        System.out.println(node);
        SampleContent deserialized = STANDARD_EVENT_MAPPER.readValue(binaryEvent, SampleContent.class);
        assertNotNull(deserialized);
    }
}