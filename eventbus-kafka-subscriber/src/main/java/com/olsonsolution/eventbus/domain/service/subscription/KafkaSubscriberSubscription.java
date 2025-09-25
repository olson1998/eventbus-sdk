package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class KafkaSubscriberSubscription implements SubscriberSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private UUID subscriptionId;

    private final KafkaEventbusManager kafkaEventbusManager;

    @Getter
    private final Map<KafkaSubscriptionMetadata, Collection<EventDestination>> subscribedDestinations =
            new ConcurrentHashMap<>();

    @Override
    public void onMetadataUpdate(KafkaSubscriptionMetadata metadata, EventDestination destination) {

    }

    @Override
    public void renew() {
        kafkaEventbusManager.renewSubscriberSubscription(subscriptionId);
    }

    @Override
    public void register() {
        subscriptionId = kafkaEventbusManager.registerSubscriber();
    }

    @Override
    public void unregister() {
        kafkaEventbusManager.unreqisterSubscription(subscriptionId);
    }

    @Override
    public void subscribe(EventDestination destination) {
        KafkaSubscriptionMetadata kafkaSubscriptionMetadata =
                kafkaEventbusManager.subscribeDestination(subscriptionId, destination);
        Collection<EventDestination> eventDestinations = subscribedDestinations.computeIfAbsent(
                kafkaSubscriptionMetadata,
                s -> new HashSet<>()
        );
        eventDestinations.add(destination);
    }

}
