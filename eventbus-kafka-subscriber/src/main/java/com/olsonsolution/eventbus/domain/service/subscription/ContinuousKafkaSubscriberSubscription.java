package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.ContinousSubscriberSubscription;
import com.olsonsolution.eventbus.domain.service.subscriber.subscription.StandardSubscriberSubscription;
import lombok.Getter;

import java.time.Duration;

public class ContinuousKafkaSubscriberSubscription extends StandardSubscriberSubscription
        implements ContinousSubscriberSubscription {

    @Getter
    private final Duration receiveInterval;

    public ContinuousKafkaSubscriberSubscription(EventbusManager eventbusManager,
                                                 Duration receiveInterval) {
        super(eventbusManager);
        this.receiveInterval = receiveInterval;
    }
}
