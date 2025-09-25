package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.ContinousSubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;

import java.time.Duration;

public class ContinuousKafkaSubscriberSubscription extends KafkaSubscriberSubscription
        implements ContinousSubscriberSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private boolean stopped;
    @Getter
    private final Duration receiveInterval;

    public ContinuousKafkaSubscriberSubscription(KafkaEventbusManager kafkaEventbusManager, Duration receiveInterval) {
        super(kafkaEventbusManager);
        this.receiveInterval = receiveInterval;
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
