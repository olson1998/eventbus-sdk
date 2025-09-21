package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface ImmediatePublisherSubscription<M extends SubscriptionMetadata> extends PublisherSubscription<M> {
}
