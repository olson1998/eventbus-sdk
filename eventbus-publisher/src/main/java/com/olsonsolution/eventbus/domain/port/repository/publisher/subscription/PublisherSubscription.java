package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface PublisherSubscription<D extends EventDestination> extends Subscription {

    D getDestination();

}
