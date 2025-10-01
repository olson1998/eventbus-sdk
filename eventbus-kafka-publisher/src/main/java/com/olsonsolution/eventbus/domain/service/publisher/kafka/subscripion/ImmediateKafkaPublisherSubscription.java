package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public class ImmediateKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ImmediatePublisherSubscription {

    public ImmediateKafkaPublisherSubscription(EventDestination destination,
                                               KafkaEventbusManager kafkaEventbusManager) {
        super(destination, kafkaEventbusManager);
    }
}
