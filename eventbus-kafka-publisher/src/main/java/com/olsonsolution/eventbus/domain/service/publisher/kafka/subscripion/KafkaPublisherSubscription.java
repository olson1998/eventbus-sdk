package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
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

    private final KafkaEventbusManager kafkaEventbusManager;

    @Override
    public void renew() {

    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {
        kafkaEventbusManager.unregisterSubscription(subscriptionId);
    }
}
