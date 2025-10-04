package com.olsonsolution.eventbus.domain.service.processor;

import com.olsonsolution.eventbus.domain.model.TestPayload;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
abstract class AbstractEventProcessor implements EventProcessor<TestPayload> {

    private final List<Throwable> errors = new ArrayList<>();

    private final List<EventMessage<TestPayload>> events = new ArrayList<>();

    private final List<CorruptedEventMessage<TestPayload>> corruptedEvents = new ArrayList<>();

    @Override
    public void onError(Throwable throwable) {
        errors.add(throwable);
    }

    @Override
    public void onEvent(EventMessage<TestPayload> eventMessage) {
        events.add(eventMessage);
    }

    @Override
    public void onCorruptedEvent(CorruptedEventMessage<TestPayload> corruptedEventMessage) {
        corruptedEvents.add(corruptedEventMessage);
    }
}
