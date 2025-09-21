package com.olsonsolution.eventbus.domain.port.stereotype.exception;

public abstract class EventDestinationValidationException extends IllegalArgumentException {

    protected EventDestinationValidationException(String s) {
        super(s);
    }
}
