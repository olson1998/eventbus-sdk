package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;

import java.time.Duration;

public class ScheduledKafkaPublisherSubscription extends KafkaPublisherSubscription
        implements ScheduledPublisherSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private final Duration dispatchDuration;

    public ScheduledKafkaPublisherSubscription(KafkaSubscriptionMetadata metadata,
                                               Duration dispatchDuration) {
        super(metadata);
        this.dispatchDuration = dispatchDuration;
    }
}
