package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDestinationValidationException;
import lombok.Getter;

@Getter
public class InvalidEventDestinationPatternException extends EventDestinationValidationException {

    private static final String MESSAGE = "Invalid destination element value=%s value does not follow pattern";

    private final String value;

    public InvalidEventDestinationPatternException(String value) {
        super(MESSAGE.formatted(value));
        this.value = value;
    }
}
