package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SubscriberSubscription extends Subscription {

    UUID getSubscriptionId();

    void onMetadataUpdate(SubscriptionMetadata metadata, EventChannel destination);

    SubscriptionMetadata subscribe(EventChannel destination);

    Map<UUID, Collection<EventChannel>> getSubscriptionChannels();

    List<SubscriptionMetadata> getSubscriptionsMetadata();

    Collection<EventChannel> collectSubscribedDestinations();

}
