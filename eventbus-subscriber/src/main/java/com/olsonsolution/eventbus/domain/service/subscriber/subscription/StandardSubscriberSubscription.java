package com.olsonsolution.eventbus.domain.service.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardSubscriberSubscription implements SubscriberSubscription {

    @Getter
    private UUID subscriptionId;

    private final EventbusManager eventbusManager;

    @Getter
    private final Map<UUID, Collection<EventChannel>> subscriptionChannels = new ConcurrentHashMap<>();

    @Getter
    private final List<SubscriptionMetadata> subscriptionsMetadata = new ArrayList<>();

    @Override
    public SubscriptionMetadata subscribe(EventChannel channel) {
        Optional<UUID> registeredChannelId = subscriptionChannels.entrySet()
                .stream()
                .filter(sub -> isSubscriptionForDestination(sub, channel))
                .map(Map.Entry::getKey)
                .findFirst();
        UUID channelId;
        if (registeredChannelId.isEmpty()) {
            channelId = eventbusManager.getChannelId(channel);
            subscriptionChannels.put(channelId, new ArrayList<>(Collections.singletonList(channel)));
        } else {
            channelId = registeredChannelId.get();
        }
        SubscriptionMetadata subscriptionMetadata = subscriptionsMetadata.stream()
                .filter(metadata -> Objects.equals(channelId, metadata.getChannelId()))
                .findFirst()
                .orElse(null);
        if (subscriptionMetadata == null) {
            subscriptionMetadata = eventbusManager.subscribeChannel(subscriptionId, channel);
            subscriptionsMetadata.add(subscriptionMetadata);
        }
        return subscriptionMetadata;
    }

    @Override
    public Collection<EventChannel> collectSubscribedDestinations() {
        return subscriptionChannels.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public void renew() {
        eventbusManager.renewSubscriberSubscription(subscriptionId);
    }

    @Override
    public void register() {
        subscriptionId = eventbusManager.registerSubscription();
    }

    @Override
    public void unregister() {
        eventbusManager.unregisterSubscription(subscriptionId);
        subscriptionChannels.clear();
        subscriptionsMetadata.clear();
    }

    @Override
    public void onMetadataUpdate(SubscriptionMetadata metadata, EventChannel destination) {

    }

    private boolean isSubscriptionForDestination(Map.Entry<UUID, Collection<EventChannel>> subscriptionChannels,
                                                 EventChannel channel) {
        return subscriptionChannels.getValue().contains(channel);
    }

}
