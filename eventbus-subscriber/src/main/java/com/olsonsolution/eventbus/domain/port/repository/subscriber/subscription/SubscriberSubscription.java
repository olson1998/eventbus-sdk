package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Map;

public interface SubscriberSubscription<M extends SubscriptionMetadata> extends Subscription<M> {

    void onMetadataUpdate(M metadata, EventDestination destination);

    M subscribe(EventDestination destination);

    Map<EventDestination, M> getSubscribedDestinations();

}
