package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface PublisherSubscription extends Subscription {

    SubscriptionMetadata getMetadata();

    EventDestination getDestination();

}
