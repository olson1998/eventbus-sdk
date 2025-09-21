package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDestinationValidationException;

public class UnknownMemberTypeException extends EventDestinationValidationException {

    private static final String MESSAGE = "Unknown member type for identifier=%s";

    public UnknownMemberTypeException(char identifier) {
        super(MESSAGE.formatted(identifier));
    }
}
