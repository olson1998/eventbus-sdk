package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;

public class OnDemandKafkaSubscriberSubscription extends KafkaSubscriberSubscription
        implements SubscriberSubscription<KafkaSubscriptionMetadata> {

    public OnDemandKafkaSubscriberSubscription(KafkaEventbusManager kafkaEventbusManager) {
        super(kafkaEventbusManager);
    }

}
