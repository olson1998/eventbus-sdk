package com.olsonsolution.eventbus.domain.service.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class StandardEventSubscriber<P extends EventProcessor> implements EventSubscriber<P> {

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

    }

    @Override
    public void unregister() {

    }

    protected abstract EventListener<SubscriberSubscription<?>, ?> createEventListener(EventDestination destination);

}
