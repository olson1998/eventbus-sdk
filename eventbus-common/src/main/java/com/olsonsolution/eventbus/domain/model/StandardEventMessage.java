package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
public class StandardEventMessage<C> implements EventMessage<C> {

    private final C content;

    @Singular("header")
    private final Map<String, Object> headers;

    private final ZonedDateTime timestamp;

}
