package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.olsonsolution.eventbus.domain.model.StandardSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestEventbusManager implements EventbusManager {

    private final String bootstrapServers;

    private final KafkaTopicManager kafkaTopicManager;

    private final List<ChannelEntry> channelEntries = new ArrayList<>();

    private final List<SubscriberEntry> subscriberEntries = new ArrayList<>();

    @Override
    public SubscriptionMetadata registerPublisher(EventChannel channel) {
        ChannelEntry channelEntry = new ChannelEntry(
                UUID.randomUUID(),
                new ArrayList<>(Collections.singleton(channel))
        );
        channelEntries.add(channelEntry);
        ZonedDateTime timestamp = ZonedDateTime.now();
        AsyncAPI asyncAPI = AsyncAPIFactory.fabricateAsyncAPI(bootstrapServers, channelEntry.eventChannels());
        createTopics(asyncAPI);
        return StandardSubscriptionMetadata.builder()
                .id(UUID.randomUUID())
                .channelId(channelEntry.channelId)
                .createdAt(timestamp)
                .expireAt(timestamp.plus(Duration.ofDays(365)))
                .apiDocs(asyncAPI)
                .build();
    }

    @Override
    public UUID getChannelId(EventChannel channel) {
        return channelEntries.stream()
                .filter(channelEntry -> channelEntry.eventChannels.contains(channel))
                .map(ChannelEntry::channelId)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public UUID registerSubscription() {
        ZonedDateTime timestamp = ZonedDateTime.now();
        SubscriberEntry subscriberEntry = new SubscriberEntry(UUID.randomUUID(), timestamp, new HashMap<>());
        subscriberEntry.setExpireAt(timestamp.plus(Duration.ofDays(365)));
        subscriberEntries.add(subscriberEntry);
        return subscriberEntry.subscriptionId;
    }

    @Override
    public SubscriptionMetadata renewPublisherSubscription(UUID subscriptionId) {
        return null;
    }

    @Override
    public void renewSubscriberSubscription(UUID subscriptionId) {

    }

    @Override
    public void unregisterSubscription(UUID subscriptionId) {
        subscriberEntries.removeIf(subscriberEntry -> subscriberEntry
                .subscriptionId
                .equals(subscriptionId)
        );
    }

    @Override
    public Collection<SubscriptionMetadata> subscribeChannels(UUID subscriptionId, Collection<EventChannel> channels) {
        SubscriberEntry subscriberEntry = subscriberEntries.stream()
                .filter(subscriber -> subscriber.subscriptionId.equals(subscriptionId))
                .findFirst()
                .orElseThrow();
        var subscribedChannels = channelEntries.stream()
                .filter(channel -> CollectionUtils.containsAll(channel.eventChannels, channels))
                .collect(Collectors.groupingBy(
                        ChannelEntry::channelId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                channelEntries -> channelEntries.stream()
                                        .flatMap(channelEntry -> channelEntry.eventChannels.stream())
                                        .toList())
                ));
        subscriberEntry.subscribedChannels.putAll(subscribedChannels);
        return subscriberEntry.subscribedChannels
                .entrySet()
                .stream()
                .map(e -> mapToMetadata(e.getKey(), subscriptionId, e.getValue()))
                .toList();
    }

    private SubscriptionMetadata mapToMetadata(UUID channelId, UUID subscriptionId, Collection<EventChannel> channels) {
        SubscriberEntry subscriberEntry = subscriberEntries.stream()
                .filter(s -> s.subscriptionId.equals(subscriptionId))
                .findFirst()
                .orElseThrow();
        return StandardSubscriptionMetadata.builder()
                .id(subscriptionId)
                .channelId(channelId)
                .createdAt(subscriberEntry.createdAt)
                .expireAt(subscriberEntry.expireAt)
                .apiDocs(AsyncAPIFactory.fabricateAsyncAPI(bootstrapServers, channels))
                .build();
    }

    private void createTopics(AsyncAPI asyncAPI) {
        MapUtils.emptyIfNull(asyncAPI.getChannels())
                .values()
                .stream()
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast)
                .filter(channel -> channel.getBindings() != null && channel.getBindings().containsKey("kafka"))
                .map(channel -> channel.getBindings().get("kafka"))
                .filter(KafkaChannelBinding.class::isInstance)
                .map(KafkaChannelBinding.class::cast)
                .forEach(kafkaChannelBinding -> kafkaTopicManager.createTopicIfNotPresent(
                        kafkaChannelBinding.getTopic(),
                        Optional.ofNullable(kafkaChannelBinding.getPartitions()).orElse(1),
                        Optional.ofNullable(kafkaChannelBinding.getReplicas())
                                .map(Integer::shortValue)
                                .orElse((short) 1)
                ));
    }

    private record ChannelEntry(UUID channelId, Collection<EventChannel> eventChannels) {

    }

    @Data
    private class SubscriberEntry {

        private final UUID subscriptionId;

        private final ZonedDateTime createdAt;

        private final Map<UUID, Collection<EventChannel>> subscribedChannels;

        private ZonedDateTime expireAt;

    }

}