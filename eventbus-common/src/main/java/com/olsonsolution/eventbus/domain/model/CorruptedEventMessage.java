package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class CorruptedEventMessage<C> implements EventMessage<C> {

    @Getter
    private final Map<String, Object> headers;

    @Getter
    private final ZonedDateTime timestamp;

    @Getter
    private byte[] binaryContent;

    @Override
    public C getContent() {
        return null;
    }

}
