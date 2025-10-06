package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.service.subscriber.subscription.StandardSubscriberSubscription;

public class OnDemandKafkaSubscriberSubscription extends StandardSubscriberSubscription {

    public OnDemandKafkaSubscriberSubscription(EventbusManager eventbusManager) {
        super(eventbusManager);
    }

}
