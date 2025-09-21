package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.time.Duration;

public interface ScheduledPublisherSubscription<M extends SubscriptionMetadata> extends PublisherSubscription<M> {

    Duration getDispatchDuration();

}
