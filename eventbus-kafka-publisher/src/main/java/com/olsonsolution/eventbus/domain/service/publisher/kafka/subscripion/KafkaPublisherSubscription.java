package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class KafkaPublisherSubscription implements PublisherSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private UUID subscriptionId;

    @Getter
    private KafkaSubscriptionMetadata metadata;

    @Getter
    private final EventDestination destination;

    private final KafkaEventbusManager kafkaEventbusManager;

    @Override
    public void renew() {
        metadata = kafkaEventbusManager.renewSubscription(subscriptionId);
    }

    @Override
    public void register() {
        metadata = kafkaEventbusManager.registerPublisher(destination);
    }

    @Override
    public void unregister() {
        kafkaEventbusManager.unreqisterSubscription(subscriptionId);
    }
}
