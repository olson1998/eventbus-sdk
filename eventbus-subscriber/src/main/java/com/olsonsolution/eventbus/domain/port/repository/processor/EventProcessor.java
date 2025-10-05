package com.olsonsolution.eventbus.domain.port.repository.processor;

import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

import java.util.Map;

public interface EventProcessor<C> {

    int onError(Throwable throwable);

    int onEvent(EventMessage<C> eventMessage);

    int onCorruptedEvent(CorruptedEventMessage<C> corruptedEventMessage);

    void onPostProcess(Map<EventMessage<C>, Integer> eventMessageProcessStatuses);

}
