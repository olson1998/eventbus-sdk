package com.olsonsolution.eventbus.domain.port.stereotype.kafka.exception;

public abstract class AbstractKafkaRuntimeException extends RuntimeException {

    protected AbstractKafkaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    protected AbstractKafkaRuntimeException(String message) {
        super(message);
    }
}
