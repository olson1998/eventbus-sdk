package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface ImmediateEventPublisher<C, D extends EventDestination>
        extends EventPublisher<C, D, ImmediatePublisherSubscription<D>> {
}
