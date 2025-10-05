package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.service.subscription.KafkaSubscriberSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
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
    private final S subscription;

    private final EventMapper<C> eventMapper;

    private final KafkaFactory kafkaFactory;

    private final ConcurrentMap<KafkaReceiver<String, C>, List<SubscriptionMetadata>> kafkaReceiversForSubscriptions =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<KafkaReceiver<String, C>, Disposable> kafkaReceiverPolls = new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventDestination destination) {
        SubscriptionMetadata metadata = subscription.subscribe(destination);
        KafkaReceiver<String, C> kafkaReceiver = findReceiverByMetadata(metadata)
                .orElseGet(() -> kafkaFactory.fabricateReceiver(
                        subscription.getSubscriptionId(),
                        destination,
                        metadata.getApiDocs(),
                        eventMapper
                ));
        kafkaReceiversForSubscriptions.computeIfAbsent(kafkaReceiver, k -> new ArrayList<>())
                .add(metadata);
    }

    @Override
    public void unsubscribe(EventDestination destination) {
        SubscriptionMetadata metadata = subscription.getSubscribedDestinations().get(destination);
        if (metadata == null) {
            return;
        }
    }

    protected void consumeAndProcess(EventProcessor<C> eventProcessor) {
        for (KafkaReceiver<String, C> kafkaReceiver : kafkaReceiversForSubscriptions.keySet()) {
            kafkaReceiverPolls.computeIfAbsent(
                    kafkaReceiver,
                    receiver -> consumeAndProcess(receiver, eventProcessor)
            );
        }
    }

    private Disposable consumeAndProcess(KafkaReceiver<String, C> kafkaReceiver, EventProcessor<C> eventProcessor) {
        return kafkaReceiver.receive()
                .map(receiverRecord -> processEvent(receiverRecord, eventProcessor))
                .collectList()
                .doOnNext(eventProcessor::onPostProcess)
                .doOnTerminate(() -> kafkaReceiverPolls.remove(kafkaReceiver))
                .subscribe();
    }

    private int processEvent(ReceiverRecord<String, C> receiverRecord,
                             EventProcessor<C> eventProcessor) {
        try {
            EventMessage<C> eventMessage = mapToEventMessage(receiverRecord);
            return eventProcessor.onEvent(eventMessage);
        } catch (Exception e) {
            return eventProcessor.onError(e);
        }
    }

    private EventMessage<C> mapToEventMessage(ReceiverRecord<String, C> receiverRecord) {
        return ConsumedKafkaEventMessage.<C>consumedKafkaEventMessageBuilder()
                .key(receiverRecord.key())
                .content(receiverRecord.value())
                .topic(receiverRecord.topic())
                .partition(receiverRecord.partition())
                .offset(receiverRecord.offset())
                .timestamp(ZonedDateTime.ofInstant(Instant.ofEpochMilli(receiverRecord.timestamp()), UTC))
                .headers(mapToHeaders(receiverRecord.headers()))
                .build();
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

    private Consumer<String, C> closeConsumer(Consumer<String, C> kafkaConsumer) {
        kafkaConsumer.close();
        return kafkaConsumer;
    }

    private Collection<TopicPartition> collectTopicPartitions(SubscriptionMetadata metadata) {
        return Collections.emptyList();
    }

    private Optional<KafkaReceiver<String, C>> findReceiverByMetadata(SubscriptionMetadata metadata) {
        return kafkaReceiversForSubscriptions.entrySet()
                .stream()
                .filter(receiverSubscriptions -> receiverSubscriptions.getValue().contains(metadata))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    @Override
    public void close() throws Exception {
        for (KafkaReceiver<String, C> kafkaReceiver : kafkaReceiversForSubscriptions.keySet()) {
            kafkaReceiver.doOnConsumer(this::closeConsumer)
                    .then()
                    .block();
        }
        closed = true;
    }
}