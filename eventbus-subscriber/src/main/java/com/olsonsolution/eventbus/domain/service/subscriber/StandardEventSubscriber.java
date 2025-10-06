package com.olsonsolution.eventbus.domain.service.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class StandardEventSubscriber<C> implements EventSubscriber<C> {

    @Getter
    private boolean closed;

    private final EventProcessor<C> eventProcessor;

    private final EventListener<C, ? extends SubscriberSubscription> eventListener;

    @Override
    public Collection<EventChannel> getSubscribedDestinations() {
        return eventListener.getSubscription()
                .getSubscribedDestinations()
                .keySet();
    }

    @Override
    public void subscribe(EventChannel destination) {
        UUID id = eventListener.getSubscription().getSubscriptionId();
        log.info("Subscriber {} Subscribing to destination: {}", id, destination);
        eventListener.subscribe(destination);
        log.info("Subscriber {} Subscribed to destination: {}", id, destination);
    }

    @Override
    public void unsubscribe(EventChannel destination) {
        UUID id = eventListener.getSubscription().getSubscriptionId();
        log.info("Subscriber {} unsubscribing to destination: {}", id, destination);
        eventListener.subscribe(destination);
        log.info("Subscriber {} unsubscribed to destination: {}", id, destination);
    }

    @Override
    public void receive() {
        eventListener.listen(eventProcessor);
    }

    @Override
    public void stopReceiving() {
        eventListener.stopListening();
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
        Collection<EventChannel> destinations = getSubscribedDestinations();
        log.info("Unregistering subscriber {} for destinations={}", id, destinations);
        eventListener.getSubscription().unregister();
        log.info("Unregistered subscriber {} for destinations={}", id, destinations);
    }

    @Override
    public void onDestinationDestroyed(EventChannel destination) {
        UUID id = eventListener.getSubscription().getSubscriptionId();
        log.info("Unsubscribing subscription {} from destination={} due to destination destruction", id, destination);
        unsubscribe(destination);
    }

    @Override
    public void close() throws Exception {
        if (eventListener.isListening()) {
            eventListener.stopListening();
        }
        eventListener.close();
        closed = true;
    }
}
