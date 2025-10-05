package com.olsonsolution.eventbus.domain.service.processor;

import com.olsonsolution.eventbus.domain.model.TestPayload;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return 200;
    }

    @Override
    public int onEvent(EventMessage<TestPayload> eventMessage) {
        log.info("Event: {} processing stared", eventMessage);
        events.add(eventMessage);
        log.info("Event: {} processing completed", eventMessage);
        return 200;
    }

    @Override
    public void onCorruptedEvent(CorruptedEventMessage<TestPayload> corruptedEventMessage) {
        log.info("Event: {} is corrupted, reason:", corruptedEventMessage, corruptedEventMessage.getCorruptionCause());
        corruptedEvents.add(corruptedEventMessage);
    }

    @Override
    public void onPostProcess(Collection<Integer> statusCodes) {
        long errorCounts = statusCodes.stream().filter(statusCode -> statusCode != 200).count();
        log.info("Events processing success rate: [{}/{}]", events.size() - errorCounts, events.size());
    }
}
