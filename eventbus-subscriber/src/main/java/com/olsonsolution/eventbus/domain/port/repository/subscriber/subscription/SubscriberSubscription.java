package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SubscriberSubscription extends Subscription {

    UUID getSubscriptionId();

    void onMetadataUpdate(SubscriptionMetadata metadata, EventChannel destination);

    SubscriptionMetadata subscribe(EventChannel destination);

    SubscriptionMetadata unsubscribe(EventChannel destination);

    Map<UUID, Collection<EventChannel>> getSubscriptionChannels();

    Map<UUID, SubscriptionMetadata> getSubscriptionsMetadata();

    Collection<EventChannel> collectSubscribedDestinations();

    Optional<UUID> findIdByChannel(EventChannel channel);

    Optional<SubscriptionMetadata> findMetadataByChannel(UUID channelId);

}
