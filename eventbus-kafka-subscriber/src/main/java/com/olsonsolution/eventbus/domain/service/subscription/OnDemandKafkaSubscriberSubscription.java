package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;

public class OnDemandKafkaSubscriberSubscription extends KafkaSubscriberSubscription {

    public OnDemandKafkaSubscriberSubscription(KafkaEventbusManager kafkaEventbusManager) {
        super(kafkaEventbusManager);
    }

}
