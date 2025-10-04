package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class KafkaSubscriberSubscription implements SubscriberSubscription {

    @Getter
    private UUID subscriptionId;

    private final EventbusManager eventbusManager;

    @Getter
    private final Map<EventDestination, SubscriptionMetadata> subscribedDestinations = new ConcurrentHashMap<>();

    @Override
    public SubscriptionMetadata subscribe(EventDestination destination) {
        return eventbusManager.subscribeDestination(subscriptionId, destination);
    }

    @Override
    public void renew() {
        eventbusManager.renewSubscriberSubscription(subscriptionId);
    }

    @Override
    public void register() {
        subscriptionId = eventbusManager.registerSubscriber();
    }

    @Override
    public void unregister() {
        eventbusManager.unregisterSubscription(subscriptionId);
    }

    @Override
    public void onMetadataUpdate(SubscriptionMetadata metadata, EventDestination destination) {

    }

}
