package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import lombok.Getter;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

@Getter
public class NoMatchingCorrelationException extends EventDispatchException {

    private static final String MESSAGE = "No matching correlation found for correlation id=%s";

    private final SenderRecord<String, ?, UUID> senderRecord;

    public NoMatchingCorrelationException(SenderRecord<String, ?, UUID> senderRecord) {
        super(MESSAGE.formatted(senderRecord.correlationMetadata()));
        this.senderRecord = senderRecord;
    }
}
