package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.StandardKafkaSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class EventbusKafkaManager implements EventbusManager<KafkaSubscriptionMetadata> {

    private final KafkaAdminClient kafkaAdmin;

    private final Map<String, Integer> nodeReplicas = new HashMap<>();

    private final Vector<KafkaSubscriptionMetadata> subscriptions = new Vector<>();

    private final Map<UUID, Collection<EventDestination>> subscriptionsEventDestinations = new ConcurrentHashMap<>();

    @Override
    public KafkaSubscriptionMetadata registerPublisher(UUID subscriptionId, EventDestination destination) {
        KafkaSubscriptionMetadata metadata = create(subscriptionId, destination);
        createTopic(metadata.getTopic(), destination.getSubscriber().getName());
        subscriptions.add(metadata);
        Collection<EventDestination> destinations =
                subscriptionsEventDestinations.computeIfAbsent(subscriptionId, k -> new Vector<>());
        destinations.add(destination);
        return metadata;
    }

    @Override
    public UUID registerSubscriber() {
        return UUID.randomUUID();
    }

    @Override
    public KafkaSubscriptionMetadata renewPublisherSubscription(UUID subscriptionId) {
        return find(subscriptionId).orElseThrow();
    }

    @Override
    public void renewSubscriberSubscription(UUID subscriptionId) {

    }

    @Override
    public void unreqisterSubscription(UUID subscriptionId) {

    }

    @Override
    public KafkaSubscriptionMetadata subscribeDestination(UUID subscriptionId, EventDestination destination) {
        KafkaSubscriptionMetadata metadata;
        if (subscriptionsEventDestinations.containsKey(subscriptionId)) {
            Collection<EventDestination> destinations = subscriptionsEventDestinations.get(subscriptionId);
            if (!destinations.contains(destination)) {
                destinations.add(destination);
                metadata = create(subscriptionId, destination);
                subscriptions.add(metadata);
            } else {
                metadata = find(subscriptionId).orElseThrow();
            }
        } else {
            throw new IllegalArgumentException("Subscription not found");
        }
        return metadata;
    }

    @SneakyThrows
    private void createTopic(String topic, String publisher) {
        Integer partitions = nodeReplicas.get(publisher);
        NewTopic newTopic = new NewTopic(topic, Objects.requireNonNullElse(partitions, 1), (short) 1);
        kafkaAdmin.createTopics(Collections.singletonList(newTopic)).all().get();
    }

    private KafkaSubscriptionMetadata create(UUID subscriptionId, EventDestination destination) {
        String topic = destination.toString();
        Integer partition = nodeReplicas.get(destination.getSubscriber().getName());
        return StandardKafkaSubscriptionMetadata.kafkaBuilder()
                .topic(topic)
                .partition(partition)
                .id(subscriptionId)
                .createdAt(ZonedDateTime.now())
                .build();
    }

    private Optional<KafkaSubscriptionMetadata> find(UUID subscriptionId) {
        return subscriptions.stream()
                .filter(meta -> subscriptionId.equals(meta.getId()))
                .findFirst();
    }

}
