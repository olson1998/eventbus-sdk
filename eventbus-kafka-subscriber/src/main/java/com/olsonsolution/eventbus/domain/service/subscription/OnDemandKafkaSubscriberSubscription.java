package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;

public class OnDemandKafkaSubscriberSubscription extends KafkaSubscriberSubscription {

    public OnDemandKafkaSubscriberSubscription(EventbusManager eventbusManager) {
        super(eventbusManager);
    }

}
