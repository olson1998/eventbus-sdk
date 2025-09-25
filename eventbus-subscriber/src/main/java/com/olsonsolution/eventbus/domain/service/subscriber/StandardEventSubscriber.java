package com.olsonsolution.eventbus.domain.service.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class StandardEventSubscriber<P extends EventProcessor> implements EventSubscriber<P> {

    private final P eventProcessor;

    private final EventListener<SubscriberSubscription<?>, ?> eventListener;

    @Override
    public Collection<EventDestination> getSubscribedDestinations() {
        return eventListener.getSubscription()
                .getSubscribedDestinations()
                .keySet();
    }

    @Override
    public void subscribe(EventDestination destination) {
        eventListener.subscribe(destination);
    }

    @Override
    public CompletableFuture<Void> receive() {
        return eventListener.receive(eventProcessor);
    }

    @Override
    public void register() {
        log.info("Registering subscriber");
        eventListener.getSubscription().register();
        log.info("Registered subscriber: {}", eventListener.getSubscription().getSubscriptionId());
    }

    @Override
    public void unregister() {
        UUID id = eventListener.getSubscription().getSubscriptionId();
        Collection<EventDestination> destinations = getSubscribedDestinations();
        log.info("Unregistering subscriber {} for destinations={}", id, destinations);
        eventListener.getSubscription().unregister();
        log.info("Unregistered subscriber {} for destinations={}", id, destinations);
    }

}
