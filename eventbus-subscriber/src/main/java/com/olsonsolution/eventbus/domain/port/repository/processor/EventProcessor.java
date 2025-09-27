package com.olsonsolution.eventbus.domain.port.repository.processor;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

public interface EventProcessor<C> {

    void onEvent(EventMessage<C> eventMessage);

}
