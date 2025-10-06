package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.ContinousSubscriberSubscription;
import lombok.Getter;

import java.time.Duration;

public class ContinuousKafkaSubscriberSubscription extends KafkaSubscriberSubscription
        implements ContinousSubscriberSubscription {

    @Getter
    private final Duration receiveInterval;

    public ContinuousKafkaSubscriberSubscription(EventbusManager eventbusManager,
                                                 Duration receiveInterval) {
        super(eventbusManager);
        this.receiveInterval = receiveInterval;
    }
}
