package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ImmediatePublisherSubscription;

public interface ImmediateEventDispatcher<C, S extends ImmediatePublisherSubscription> extends EventDispatcher<C, S> {
}
