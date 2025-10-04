package com.olsonsolution.eventbus.domain.model.kafka.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.kafka.exception.AbstractKafkaRuntimeException;

public class KafkaSerializationException extends AbstractKafkaRuntimeException {

    private static final String MESSAGE = "Kafka message was not serialized successfully";

    public KafkaSerializationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
