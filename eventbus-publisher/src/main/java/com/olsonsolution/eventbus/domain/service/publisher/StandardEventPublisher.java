package com.olsonsolution.eventbus.domain.service.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventPublisher;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class StandardEventPublisher<C> implements EventPublisher<C> {

    @Getter
    private UUID id;

    private final EventDispatcher<C, PublisherSubscription<?>, ?> eventDispatcher;

    @Getter
    private final EventDestination destination;

    @Override
    public CompletableFuture<List<EventAcknowledgment>> publish(EventMessage<C> message) {
        return eventDispatcher.dispatch(message)
                .toFuture();
    }

    @Override
    public void register() {
        log.info("Registering publisher for destination={}", destination);
        id = eventDispatcher.getSubscription().registerPublisher();
        log.info("Registered publisher {} for destination={}", id, destination);
    }

    @Override
    public void unregister() {
        log.info("Unregistering publisher {} for destination={}", id, destination);
        eventDispatcher.getSubscription().unregisterPublisher();
        log.info("Unregistered publisher {} for destination={}", id, destination);
    }
}
