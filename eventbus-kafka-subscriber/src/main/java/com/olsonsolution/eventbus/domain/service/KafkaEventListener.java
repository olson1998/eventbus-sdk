package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.ConsumedCorruptedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.model.kafka.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import com.olsonsolution.eventbus.domain.service.subscription.KafkaSubscriberSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private final ConcurrentMap<KafkaReceiver<String, EventMessage<C>>, List<SubscriptionMetadata>> kafkaReceiversSubs =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<KafkaReceiver<String, EventMessage<C>>, Disposable> kafkaReceiverPolls =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventDestination destination) {
        SubscriptionMetadata metadata = subscription.subscribe(destination);
        KafkaReceiver<String, EventMessage<C>> kafkaReceiver = findReceiverByMetadata(metadata)
                .orElseGet(() -> kafkaFactory.fabricateReceiver(
                        subscription.getSubscriptionId(),
                        destination,
                        metadata.getApiDocs(),
                        eventMapper
                ));
        kafkaReceiversSubs.computeIfAbsent(kafkaReceiver, k -> new ArrayList<>())
                .add(metadata);
    }

    @Override
    public void unsubscribe(EventDestination destination) {
        SubscriptionMetadata metadata = subscription.getSubscribedDestinations().get(destination);
        if (metadata == null) {
            return;
        }
        KafkaReceiver<String, EventMessage<C>> kafkaReceiver = findReceiverByMetadata(metadata)
                .orElseThrow();
        kafkaReceiver.doOnConsumer(consumer -> {
            subscription.getSubscribedDestinations().remove(destination);
            consumer.assign(KafkaAsyncAPIUtils.collectTopicPartitions(metadata.getApiDocs()));
            return consumer;
        }).subscribe();
    }

    protected void consumeAndProcess(EventProcessor<C> eventProcessor) {
        for (KafkaReceiver<String, EventMessage<C>> kafkaReceiver : kafkaReceiversSubs.keySet()) {
            kafkaReceiverPolls.computeIfAbsent(
                    kafkaReceiver,
                    receiver -> consumeAndProcess(receiver, eventProcessor)
            );
        }
    }

    private Disposable consumeAndProcess(KafkaReceiver<String, EventMessage<C>> kafkaReceiver,
                                         EventProcessor<C> eventProcessor) {
        return kafkaReceiver.receive()
                .map(receiverRecord -> processEvent(receiverRecord, eventProcessor))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue))
                .doOnNext(eventProcessor::onPostProcess)
                .doOnTerminate(() -> kafkaReceiverPolls.remove(kafkaReceiver))
                .subscribe();
    }

    private Map.Entry<EventMessage<C>, Integer> processEvent(ReceiverRecord<String, EventMessage<C>> receiverRecord,
                                                             EventProcessor<C> eventProcessor) {
        int processStatus;
        KafkaEventMessage<C> kafkaEventMessage = mapToKafkaEventMessage(receiverRecord);
        try {
            processStatus = processKafkaEvent(kafkaEventMessage, eventProcessor);
        } catch (Exception e) {
            processStatus = eventProcessor.onError(e);
        }
        return new DefaultMapEntry<>(kafkaEventMessage, processStatus);
    }

    private int processKafkaEvent(KafkaEventMessage<C> kafkaEventMessage, EventProcessor<C> eventProcessor) {
        if (kafkaEventMessage instanceof ConsumedCorruptedKafkaEventMessage<C> corruptedKafkaEventMessage) {
            return eventProcessor.onCorruptedEvent(corruptedKafkaEventMessage);
        } else {
            return eventProcessor.onEvent(kafkaEventMessage);
        }
    }

    private KafkaEventMessage<C> mapToKafkaEventMessage(ReceiverRecord<String, EventMessage<C>> receiverRecord) {
        EventMessage<C> eventMessage = receiverRecord.value();
        Map<String, Object> headers = mapToHeaders(receiverRecord.headers());
        ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(receiverRecord.timestamp()), UTC);
        if (eventMessage instanceof CorruptedEventMessage<C> corruptedEventMessage) {
            return ConsumedCorruptedKafkaEventMessage.<C>kafkaCorruptedEventMessageBuilder()
                    .corruptionCause(corruptedEventMessage.getCorruptionCause())
                    .key(receiverRecord.key())
                    .headers(headers)
                    .topic(receiverRecord.topic())
                    .partition(receiverRecord.partition())
                    .offset(receiverRecord.offset())
                    .timestamp(timestamp)
                    .build();
        }
        return ConsumedKafkaEventMessage.<C>kafkaEventMessageBuilder()
                .content(eventMessage.getContent())
                .key(receiverRecord.key())
                .headers(headers)
                .topic(receiverRecord.topic())
                .partition(receiverRecord.partition())
                .offset(receiverRecord.offset())
                .timestamp(timestamp)
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

    private Optional<KafkaReceiver<String, EventMessage<C>>> findReceiverByMetadata(SubscriptionMetadata metadata) {
        return kafkaReceiversSubs.entrySet()
                .stream()
                .filter(receiverSubscriptions -> receiverSubscriptions.getValue()
                        .contains(metadata))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    @Override
    public void close() throws Exception {
        for (KafkaReceiver<String, EventMessage<C>> kafkaReceiver : kafkaReceiversSubs.keySet()) {
            kafkaReceiver.doOnConsumer(consumer -> {
                consumer.close();
                return consumer;
            }).subscribe();
        }
        closed = true;
    }
}