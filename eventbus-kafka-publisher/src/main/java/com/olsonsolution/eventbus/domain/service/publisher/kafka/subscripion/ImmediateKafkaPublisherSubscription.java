package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public class ImmediateKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ImmediatePublisherSubscription {

    public ImmediateKafkaPublisherSubscription(EventDestination destination,
                                               EventbusManager eventbusManager) {
        super(destination, eventbusManager);
    }
}
