package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.time.Duration;

public interface ScheduledPublisherSubscription<D extends EventDestination> extends PublisherSubscription<D> {

    Duration getDispatchDuration();

}
