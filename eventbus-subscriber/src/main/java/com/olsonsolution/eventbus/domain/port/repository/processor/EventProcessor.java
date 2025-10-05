package com.olsonsolution.eventbus.domain.port.repository.processor;

import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

import java.util.Collection;

public interface EventProcessor<C> {

    int onError(Throwable throwable);

    int onEvent(EventMessage<C> eventMessage);

    void onCorruptedEvent(CorruptedEventMessage<C> corruptedEventMessage);

    void onPostProcess(Collection<Integer> statusCodes);

}
