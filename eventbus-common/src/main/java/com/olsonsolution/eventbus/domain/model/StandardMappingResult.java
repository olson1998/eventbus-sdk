package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardMappingResult<T> implements MappingResult<T> {

    private final T result;

    private final EventMappingException exception;

    @Override
    public T getOrThrow() throws EventMappingException {
        if (exception != null) {
            throw exception;
        }
        return result;
    }
}
