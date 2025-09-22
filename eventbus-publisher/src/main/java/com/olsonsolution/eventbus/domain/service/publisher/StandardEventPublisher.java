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

    private final EventDispatcher<C, PublisherSubscription<?>, ?> eventDispatcher;

    @Getter
    private final EventDestination destination;

    @Override
    public CompletableFuture<List<EventAcknowledgment>> publish(EventMessage<C> message) {
        return eventDispatcher.dispatch(message)
                .doOnNext(this::logEventAcknowledged)
                .doOnError(this::logEventDispatchFailed)
                .toFuture();
    }

    @Override
    public void register() {
        log.info("Registering publisher for destination={}", destination);
        eventDispatcher.getSubscription().register();
        UUID id = eventDispatcher.getSubscription().getSubscriptionId();
        log.info("Registered publisher {} for destination={}", id, destination);
    }

    @Override
    public void unregister() {
        UUID id = eventDispatcher.getSubscription().getSubscriptionId();
        log.info("Unregistering publisher {} for destination={}", id, destination);
        eventDispatcher.getSubscription().unregister();
        log.info("Unregistered publisher {} for destination={}", id, destination);
    }

    private void logEventAcknowledged(List<EventAcknowledgment> acknowledgments) {
        log.info("Event acknowledged for destination={} with {} acknowledgments", destination, acknowledgments.size());
    }

    private void logEventDispatchFailed(Throwable throwable) {
        log.error("Event dispatch failed for destination={}, reason:", destination, throwable);
    }

}
