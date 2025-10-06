package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.ConsumedCorruptedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.model.kafka.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import com.olsonsolution.eventbus.domain.service.subscription.KafkaSubscriberSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Map.entry;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventListener<C, S extends KafkaSubscriberSubscription> implements EventListener<C, S> {

    @Getter
    private boolean closed;

    @Getter
    private boolean listening;

    protected final Duration maxPollInterval;

    @Getter
    private final S subscription;

    private final Logger log;

    private final EventMapper<C> eventMapper;

    private final KafkaFactory kafkaFactory;

    private final ConcurrentMap<UUID, Disposable> consumerSubscriptions = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, ReceiverOptions<String, MappingResult<C>>> subscriptionReceiverOptions =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, KafkaReceiver<String, MappingResult<C>>> subscriptionReceivers =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventDestination destination) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = subscription.subscribe(destination);
        UUID subscriptionId = metadata.getId();
        if (!subscriptionReceiverOptions.containsKey(subscriptionId)) {
            ReceiverOptions<String, MappingResult<C>> receiverOptions = kafkaFactory.fabricateReceiver(
                    maxPollInterval,
                    subscriptionId,
                    destination,
                    metadata.getApiDocs(),
                    eventMapper
            );
            receiverOptions.addAssignListener(this::onAssignment);
            KafkaReceiver<String, MappingResult<C>> kafkaReceiver = KafkaReceiver.create(receiverOptions);
            subscriptionReceiverOptions.put(subscriptionId, receiverOptions);
            subscriptionReceivers.put(subscriptionId, kafkaReceiver);
        }
    }

    @Override
    public void unsubscribe(EventDestination destination) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = subscription.getSubscribedDestinations().get(destination);
        if (metadata == null) {
            return;
        }
        UUID subscriptionId = metadata.getId();
        if (subscriptionReceivers.containsKey(subscriptionId)) {
            KafkaReceiver<String, MappingResult<C>> kafkaReceiver = subscriptionReceivers.get(subscriptionId);
            kafkaReceiver.doOnConsumer(consumer -> {
                subscription.getSubscribedDestinations().remove(destination);
                consumer.assign(KafkaAsyncAPIUtils.collectTopicPartitions(metadata.getApiDocs()));
                return consumer;
            }).subscribe();
        }
    }

    @Override
    public void listen(EventProcessor<C> eventProcessor) {
        assertIsNotClosed();
        listening = true;
        for (Map.Entry<UUID, KafkaReceiver<String, MappingResult<C>>> subscriptionReceiver :
                subscriptionReceivers.entrySet()) {
            UUID subscriptionId = subscriptionReceiver.getKey();
            KafkaReceiver<String, MappingResult<C>> kafkaReceiver = subscriptionReceiver.getValue();
            consumerSubscriptions.computeIfAbsent(
                    subscriptionId,
                    sid -> subscribeToReceiver(kafkaReceiver, eventProcessor)
            );
        }
    }

    @Override
    public void stopListening() {
        assertIsNotClosed();
        for (Map.Entry<UUID, ReceiverOptions<String, MappingResult<C>>> subscriptionReceiverOps :
                subscriptionReceiverOptions.entrySet()) {
            UUID subscriptionId = subscriptionReceiverOps.getKey();
            log.info("Subscriber: {} Stopping listening to subscription: {}",
                    subscription.getSubscriptionId(), subscriptionId);
            ReceiverOptions<String, MappingResult<C>> receiverOptions = subscriptionReceiverOps.getValue();
            receiverOptions.assignment(CollectionUtils.emptyCollection());
        }
        listening = false;
    }

    protected abstract Disposable subscribeToReceiver(KafkaReceiver<String, MappingResult<C>> kafkaReceiver,
                                                      EventProcessor<C> eventProcessor);

    protected void assertIsNotClosed() {
        if (closed) {
            throw new IllegalStateException("Event listener is closed");
        }
    }

    protected Collection<KafkaReceiver<String, MappingResult<C>>> getKafkaReceivers() {
        return subscriptionReceivers.values();
    }

    protected void processEvent(ReceiverRecord<String, MappingResult<C>> receiverRecord,
                                EventProcessor<C> eventProcessor) {
        KafkaEventMessage<C> kafkaEventMessage = mapToKafkaEventMessage(receiverRecord);
        try {
            processKafkaEvent(kafkaEventMessage, eventProcessor);
        } catch (Exception e) {
            eventProcessor.onError(e);
        }
        receiverRecord.receiverOffset().acknowledge();
    }

    protected void disposeSubscriptions() {
        for (Disposable kafkaConsumerSubscription : consumerSubscriptions.values()) {
            if (kafkaConsumerSubscription.isDisposed()) {
                kafkaConsumerSubscription.dispose();
            }
        }
    }

    private void processKafkaEvent(KafkaEventMessage<C> kafkaEventMessage, EventProcessor<C> eventProcessor) {
        if (kafkaEventMessage instanceof ConsumedCorruptedKafkaEventMessage<C> corruptedKafkaEventMessage) {
            eventProcessor.onCorruptedEvent(corruptedKafkaEventMessage);
        } else {
            eventProcessor.onEvent(kafkaEventMessage);
        }
    }

    protected void onAssignment(Collection<ReceiverPartition> receiverPartitions) {
        Collection<TopicPartition> topicPartitions =
                CollectionUtils.collect(receiverPartitions, ReceiverPartition::topicPartition);
        log.info("Subscriber: {} Assigned to partitions: {}", subscription.getSubscriptionId(), topicPartitions);
    }

    private KafkaEventMessage<C> mapToKafkaEventMessage(ReceiverRecord<String, MappingResult<C>> receiverRecord) {
        MappingResult<C> mappingResult = receiverRecord.value();
        Map<String, Object> headers = mapToHeaders(receiverRecord.headers());
        ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(receiverRecord.timestamp()), UTC);
        try {
            C content = mappingResult.getOrThrow();
            return ConsumedKafkaEventMessage.<C>kafkaEventMessageBuilder()
                    .content(content)
                    .key(receiverRecord.key())
                    .headers(headers)
                    .topic(receiverRecord.topic())
                    .partition(receiverRecord.partition())
                    .offset(receiverRecord.offset())
                    .timestamp(timestamp)
                    .build();
        } catch (EventMappingException e) {
            return ConsumedCorruptedKafkaEventMessage.<C>kafkaCorruptedEventMessageBuilder()
                    .corruptionCause(e)
                    .key(receiverRecord.key())
                    .headers(headers)
                    .topic(receiverRecord.topic())
                    .partition(receiverRecord.partition())
                    .offset(receiverRecord.offset())
                    .timestamp(timestamp)
                    .build();
        }
    }

    private Map<String, Object> mapToHeaders(Headers headers) {
        return IteratorUtils.toList(headers.iterator())
                .stream()
                .map(this::mapToHeader)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                        MapUtils::unmodifiableMap
                ));
    }

    private Map.Entry<String, Object> mapToHeader(Header header) {
        return entry(header.key(), new String(header.value()));
    }

    @Override
    public void close() throws Exception {
        disposeSubscriptions();
        closed = true;
    }
}