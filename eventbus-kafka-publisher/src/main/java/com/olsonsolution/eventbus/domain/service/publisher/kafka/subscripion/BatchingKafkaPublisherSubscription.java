package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.BatchingPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;

import java.time.Duration;

public class BatchingKafkaPublisherSubscription extends KafkaPublisherSubscription implements BatchingPublisherSubscription {

    @Getter
    private final int batchSize;

    @Getter
    private final Duration inactivityTimeout;

    public BatchingKafkaPublisherSubscription(KafkaSubscriptionMetadata metadata,
                                              int batchSize,
                                              Duration inactivityTimeout) {
        super(metadata);
        this.batchSize = batchSize;
        this.inactivityTimeout = inactivityTimeout;
    }
}
