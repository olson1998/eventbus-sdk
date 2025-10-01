package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.KafkaEventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.BatchingPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import lombok.Getter;

import java.time.Duration;

public class BatchingKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements BatchingPublisherSubscription {

    @Getter
    private final int batchSize;

    @Getter
    private final Duration inactivityTimeout;

    public BatchingKafkaPublisherSubscription(EventDestination destination,
                                              KafkaEventbusManager kafkaEventbusManager,
                                              int batchSize,
                                              Duration inactivityTimeout) {
        super(destination, kafkaEventbusManager);
        this.batchSize = batchSize;
        this.inactivityTimeout = inactivityTimeout;
    }
}
