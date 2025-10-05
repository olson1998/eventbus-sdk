package com.olsonsolution.eventbus.domain.port.stereotype;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;

public interface CorruptedEventMessage<T> extends EventMessage<T> {

    EventMappingException getCorruptionCause();

}
