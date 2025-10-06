package com.olsonsolution.eventbus.domain.port.stereotype.exception;

import java.io.IOException;

public abstract class EventMappingException extends IOException {

    protected EventMappingException(String message) {
        super(message);
    }

    protected EventMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
