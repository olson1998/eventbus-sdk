package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.time.Duration;

public interface BatchingPublisherSubscription<M extends SubscriptionMetadata> extends PublisherSubscription<M> {

    int getBatchSize();

    Duration getInactivityTimeout();

}
