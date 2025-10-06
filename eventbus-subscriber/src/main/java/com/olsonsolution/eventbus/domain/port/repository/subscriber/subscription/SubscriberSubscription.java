package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Map;
import java.util.Set;

public interface SubscriberSubscription extends Subscription {

    void onMetadataUpdate(SubscriptionMetadata metadata, EventDestination destination);

    SubscriptionMetadata subscribe(EventDestination destination);

    Map<EventDestination, SubscriptionMetadata> getSubscribedDestinations();

}
