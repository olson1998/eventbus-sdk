package com.olsonsolution.eventbus.domain.model.kafka.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.kafka.exception.AbstractKafkaRuntimeException;

public class KafkaDeserializationException extends AbstractKafkaRuntimeException {

    private static final String MESSAGE = "Kafka message was not deserialized successfully";

    public KafkaDeserializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
