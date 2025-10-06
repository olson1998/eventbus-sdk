package com.olsonsolution.eventbus.domain.port.stereotype;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;

public interface MappingResult<T> {

    T getOrThrow() throws EventMappingException;

}
