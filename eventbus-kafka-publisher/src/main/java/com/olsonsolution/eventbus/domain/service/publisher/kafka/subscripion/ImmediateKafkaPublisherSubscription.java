package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;

public class ImmediateKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ImmediatePublisherSubscription<KafkaSubscriptionMetadata> {

    public ImmediateKafkaPublisherSubscription(KafkaSubscriptionMetadata metadata) {
        super(metadata);
    }
}
