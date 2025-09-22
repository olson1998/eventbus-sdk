package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Collection;

public interface SubscriberSubscription<M extends SubscriptionMetadata> extends Subscription<M> {

    Collection<? extends EventDestination> getSubscribedDestinations();

}
