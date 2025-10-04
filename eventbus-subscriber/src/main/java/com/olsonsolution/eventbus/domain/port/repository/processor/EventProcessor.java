package com.olsonsolution.eventbus.domain.port.repository.processor;

import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

public interface EventProcessor<C> {

    void onError(Throwable throwable);

    void onEvent(EventMessage<C> eventMessage);

    void onCorruptedEvent(CorruptedEventMessage<C> corruptedEventMessage);

}
