package com.olsonsolution.eventbus.domain.service.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

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
    private final Map<UUID, SubscriptionMetadata> subscriptionsMetadata = new ConcurrentHashMap<>();

    @Override
    public SubscriptionMetadata subscribe(EventChannel channel) {
        Optional<Map.Entry<UUID, Collection<EventChannel>>> registeredChannel = subscriptionChannels.entrySet()
                .stream()
                .filter(sub -> isSubscriptionForDestination(sub, channel))
                .findFirst();
        UUID channelId;
        Collection<EventChannel> channels;
        if (registeredChannel.isEmpty()) {
            channelId = eventbusManager.getChannelId(channel);
            channels = new ArrayList<>(Collections.singletonList(channel));
            subscriptionChannels.put(channelId, channels);
        } else {
            channelId = registeredChannel.get().getKey();
            channels = registeredChannel.get().getValue();
        }
        SubscriptionMetadata subscriptionMetadata =
                CollectionUtils.extractSingleton(eventbusManager.subscribeChannels(subscriptionId, channels));
        subscriptionsMetadata.put(channelId, subscriptionMetadata);
        return subscriptionMetadata;
    }

    @Override
    public SubscriptionMetadata unsubscribe(EventChannel destination) {
        UUID channelId = findIdByChannel(destination).orElseThrow();
        Collection<EventChannel> channels = subscriptionChannels.get(channelId);
        channels.remove(destination);
        if (CollectionUtils.isNotEmpty(channels)) {
            SubscriptionMetadata subscriptionMetadata =
                    CollectionUtils.extractSingleton(eventbusManager.subscribeChannels(subscriptionId, channels));
            subscriptionsMetadata.replace(channelId, subscriptionMetadata);
            return subscriptionMetadata;
        } else {
            subscriptionChannels.remove(channelId);
            return null;
        }
    }

    @Override
    public Collection<EventChannel> collectSubscribedDestinations() {
        return subscriptionChannels.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Optional<UUID> findIdByChannel(EventChannel channel) {
        return subscriptionChannels.entrySet()
                .stream()
                .filter(sub -> isSubscriptionForDestination(sub, channel))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public Optional<SubscriptionMetadata> findMetadataByChannel(UUID channelId) {
        return Optional.ofNullable(subscriptionsMetadata.get(channelId));
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
