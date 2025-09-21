package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface ImmediatePublisherSubscription<D extends EventDestination> extends PublisherSubscription<D> {
}
