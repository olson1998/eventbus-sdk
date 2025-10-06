package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import lombok.Getter;

import java.time.Duration;

public class ScheduledKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ScheduledPublisherSubscription {

    @Getter
    private final Duration dispatchDuration;

    public ScheduledKafkaPublisherSubscription(EventChannel destination,
                                               EventbusManager eventbusManager,
                                               Duration dispatchDuration) {
        super(destination, eventbusManager);
        this.dispatchDuration = dispatchDuration;
    }

}
