package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class KafkaPublisherSubscription implements PublisherSubscription {

    @Getter
    private UUID subscriptionId;

    @Getter
    private SubscriptionMetadata metadata;

    @Getter
    private final EventDestination destination;

    private final EventbusManager eventbusManager;

    @Override
    public void renew() {

    }

    @Override
    public void register() {
        metadata = eventbusManager.registerPublisher(destination);
        subscriptionId = metadata.getId();
    }

    @Override
    public void unregister() {
        eventbusManager.unregisterSubscription(subscriptionId);
        subscriptionId = null;
    }
}
