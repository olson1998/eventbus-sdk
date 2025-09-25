package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Collection;
import java.util.Map;

public interface SubscriberSubscription<M extends SubscriptionMetadata> extends Subscription<M> {

    void onMetadataUpdate(M metadata, EventDestination destination);

    void subscribe(EventDestination destination);

    Map<M, Collection<EventDestination>> getSubscribedDestinations();

}
