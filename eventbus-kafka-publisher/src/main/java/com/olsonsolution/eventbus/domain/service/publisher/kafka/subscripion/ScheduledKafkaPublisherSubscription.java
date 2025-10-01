package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import lombok.Getter;

import java.time.Duration;

public class ScheduledKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ScheduledPublisherSubscription {

    @Getter
    private final Duration dispatchDuration;

    public ScheduledKafkaPublisherSubscription(EventDestination destination,
                                               KafkaEventbusManager kafkaEventbusManager,
                                               Duration dispatchDuration) {
        super(destination, kafkaEventbusManager);
        this.dispatchDuration = dispatchDuration;
    }

}
