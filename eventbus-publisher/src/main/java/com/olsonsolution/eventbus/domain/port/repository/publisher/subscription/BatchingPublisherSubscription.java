package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.time.Duration;

public interface BatchingPublisherSubscription extends PublisherSubscription {

    int getBatchSize();

    Duration getInactivityTimeout();

}
