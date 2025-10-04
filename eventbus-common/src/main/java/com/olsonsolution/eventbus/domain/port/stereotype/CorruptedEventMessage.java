package com.olsonsolution.eventbus.domain.port.stereotype;

public interface CorruptedEventMessage<T> extends EventMessage<T> {

    Throwable getCorruptionCause();

}
