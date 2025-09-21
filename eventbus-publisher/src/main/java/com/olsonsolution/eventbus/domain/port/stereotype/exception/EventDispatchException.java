package com.olsonsolution.eventbus.domain.port.stereotype.exception;

public abstract class EventDispatchException extends Exception {

    public EventDispatchException(String message) {
        super(message);
    }

    public EventDispatchException(String message, Throwable cause) {
        super(message, cause);
    }

}
