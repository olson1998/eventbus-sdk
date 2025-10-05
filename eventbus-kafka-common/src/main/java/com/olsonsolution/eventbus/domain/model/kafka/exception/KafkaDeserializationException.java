package com.olsonsolution.eventbus.domain.model.kafka.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;

public class KafkaDeserializationException extends EventMappingException {

    private static final String MESSAGE = "Kafka message was not deserialized successfully";

    public KafkaDeserializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
