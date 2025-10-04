package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.olsonsolution.eventbus.domain.model.StandardSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

@RequiredArgsConstructor
public class TestEventbusManager implements EventbusManager {

    private final Duration subscriptionDuration;

    private final String bootstrapServers;

    private final KafkaTopicManager kafkaTopicManager;

    private final List<SubscriberEntry> subscriberRegistry = new ArrayList<>();

    private final List<SubscriptionEntry> subscriptionRegistry = new ArrayList<>();

    @Override
    public SubscriptionMetadata registerPublisher(UUID subscriptionId, EventDestination destination) {
        Optional<SubscriptionEntry> createdSubscription = findSubscriptionEntry(destination);
        if (createdSubscription.isPresent()) {
            return createdSubscription.get().metadata();
        }
        ZonedDateTime timestamp = ZonedDateTime.now();
        SubscriptionMetadata metadata = StandardSubscriptionMetadata.builder()
                .id(subscriptionId)
                .createdAt(timestamp)
                .expireAt(timestamp.plus(subscriptionDuration))
                .apiDocs(AsyncAPIFactory.fabricateAsyncAPI(bootstrapServers, Collections.singleton(destination)))
                .build();
        SubscriptionEntry subscriptionEntry = new SubscriptionEntry(metadata, Collections.singletonList(destination));
        subscriptionRegistry.add(subscriptionEntry);
        createTopics(metadata.getApiDocs());
        return metadata;
    }

    @Override
    public UUID registerSubscriber() {
        SubscriberEntry subscriberEntry =
                new SubscriberEntry(UUID.randomUUID(), ZonedDateTime.now(), new ArrayList<>());
        subscriberRegistry.add(subscriberEntry);
        return subscriberEntry.id();
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

    }

    @Override
    public SubscriptionMetadata subscribeDestination(UUID subscriptionId, EventDestination destination) {
        SubscriberEntry subscriberEntry = findSubscriberEntry(subscriptionId).orElseThrow();
        SubscriptionEntry subscriptionEntry = findSubscriptionEntry(destination).orElseThrow();
        subscriberEntry.destinations().add(destination);
        return subscriptionEntry.metadata();
    }

    private void createTopics(AsyncAPI apiDocs) {
        Collection<KafkaChannelBinding> kafkaChannelBindings = MapUtils.emptyIfNull(apiDocs.getChannels())
                .values()
                .stream()
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast)
                .flatMap(channel -> MapUtils.emptyIfNull(channel.getBindings()).values().stream())
                .filter(KafkaChannelBinding.class::isInstance)
                .map(KafkaChannelBinding.class::cast)
                .toList();
        kafkaChannelBindings.forEach(channelBinding -> {
            int partitions = channelBinding.getPartitions() != null ? channelBinding.getPartitions() : 1;
            short replicationFactory = channelBinding.getReplicas() != null ?
                    (short) channelBinding.getReplicas().intValue() : 1;
            kafkaTopicManager.createTopicIfNotPresent(
                    channelBinding.getTopic(),
                    partitions,
                    replicationFactory
            );
        });
    }

    private Optional<SubscriberEntry> findSubscriberEntry(UUID subscriptionId) {
        return subscriberRegistry.stream()
                .filter(entry -> entry.id().equals(subscriptionId))
                .findFirst();
    }

    private List<SubscriberEntry> listSubscriberEntry(EventDestination destination) {
        return subscriberRegistry.stream()
                .filter(entry -> entry.destinations().contains(destination))
                .toList();
    }

    private Optional<SubscriptionEntry> findSubscriptionEntry(UUID subscriptionId) {
        return subscriptionRegistry.stream()
                .filter(entry -> entry.metadata().getId().equals(subscriptionId))
                .findFirst();
    }

    private Optional<SubscriptionEntry> findSubscriptionEntry(EventDestination destination) {
        return subscriptionRegistry.stream()
                .filter(entry -> entry.destinations.contains(destination))
                .findFirst();
    }

    private record SubscriptionEntry(SubscriptionMetadata metadata, List<EventDestination> destinations) {

    }

    private record SubscriberEntry(UUID id, ZonedDateTime createdAt, List<EventDestination> destinations) {

    }

}
