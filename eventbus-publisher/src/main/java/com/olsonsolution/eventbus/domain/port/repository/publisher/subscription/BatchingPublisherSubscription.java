package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.time.Duration;

public interface BatchingPublisherSubscription<D extends EventDestination> extends PublisherSubscription<D> {

    int getBatchSize();

    Duration getInactivityTimeout();

}
