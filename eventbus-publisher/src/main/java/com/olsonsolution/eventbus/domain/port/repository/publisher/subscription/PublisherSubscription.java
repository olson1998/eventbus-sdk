package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface PublisherSubscription<M extends SubscriptionMetadata> extends Subscription<M> {

    M getMetadata();

    EventDestination getDestination();

}
