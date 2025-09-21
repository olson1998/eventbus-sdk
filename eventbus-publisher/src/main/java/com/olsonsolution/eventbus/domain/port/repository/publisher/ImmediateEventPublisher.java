package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface ImmediateEventPublisher<C, S extends ImmediatePublisherSubscription<M>, M extends SubscriptionMetadata>
        extends EventPublisher<C, S, M> {
}
