package com.olsonsolution.eventbus.domain.service.processor;

import com.olsonsolution.eventbus.domain.model.TestPayload;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class TestEventProcessor implements EventProcessor<TestPayload> {

    private final List<Throwable> errors = new ArrayList<>();

    private final List<EventMessage<TestPayload>> events = new ArrayList<>();

    private final List<CorruptedEventMessage<TestPayload>> corruptedEvents = new ArrayList<>();

    @Override
    public int onError(Throwable throwable) {
        log.info("Event was not processed successfully, reason:", throwable);
        errors.add(throwable);
        return 500;
    }

    @Override
    public int onEvent(EventMessage<TestPayload> eventMessage) {
        log.info("Event: {} processing stared", eventMessage);
        events.add(eventMessage);
        log.info("Event: {} processing completed", eventMessage);
        return 200;
    }

    @Override
    public int onCorruptedEvent(CorruptedEventMessage<TestPayload> corruptedEventMessage) {
        log.info("Event: {} is corrupted, reason:", corruptedEventMessage, corruptedEventMessage.getCorruptionCause());
        corruptedEvents.add(corruptedEventMessage);
        return 400;
    }

    @Override
    public void onPostProcess(Map<EventMessage<TestPayload>, Integer> eventMessageProcessStatuses) {

    }

}
