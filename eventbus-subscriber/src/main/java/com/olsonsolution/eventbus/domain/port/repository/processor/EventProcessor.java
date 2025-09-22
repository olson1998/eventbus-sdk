package com.olsonsolution.eventbus.domain.port.repository.processor;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

public interface EventProcessor {

    void onEvent(EventMessage<?> eventMessage);

}
