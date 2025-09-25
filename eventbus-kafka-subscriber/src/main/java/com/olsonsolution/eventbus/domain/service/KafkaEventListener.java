package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.service.subscription.KafkaSubscriberSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Map.entry;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventListener<S extends KafkaSubscriberSubscription>
        implements EventListener<S, KafkaSubscriptionMetadata> {

    @Getter
    private boolean closed;

    @Getter
    private final S subscription;

    private final KafkaReceiver<String, Object> kafkaReceiver;

    @Override
    public void subscribe(EventDestination destination) {
        KafkaSubscriptionMetadata metadata = subscription.subscribe(destination);
        onPositionOnTopicPartition(metadata);
    }

    protected Flux<EventMessage<?>> consume() {
        return kafkaReceiver.receive()
                .map(this::mapToEventMessage);
    }

    protected Mono<Integer> processEventAndEmitStatus(EventMessage<?> eventMessage, EventProcessor eventProcessor) {
        return Mono.fromSupplier(() -> processEvent(eventMessage, eventProcessor))
                .doOnError(this::onErrorLog)
                .onErrorResume(Exception.class, e -> Mono.just(500));
    }

    private int processEvent(EventMessage<?> eventMessage, EventProcessor eventProcessor) {
        eventProcessor.onEvent(eventMessage);
        return 200;
    }

    private EventMessage<?> mapToEventMessage(ReceiverRecord<String, Object> receiverRecord) {
        return ConsumedKafkaEventMessage.consumedKafkaEventBuilder()
                .topic(receiverRecord.topic())
                .offset(receiverRecord.offset())
                .partition(receiverRecord.partition())
                .timestamp(ZonedDateTime.ofInstant(Instant.ofEpochMilli(receiverRecord.timestamp()), UTC))
                .content(receiverRecord.value())
                .headers(mapToHeaders(receiverRecord.headers()))
                .build();
    }

    private Map<String, Object> mapToHeaders(Headers headers) {
        return IteratorUtils.toList(headers.iterator())
                .stream()
                .map(this::mapToHeader)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<String, Object> mapToHeader(Header header) {
        return entry(header.key(), new String(header.value()));
    }

    private void onErrorLog(Throwable throwable) {
        UUID subscriptionId = getSubscription().getSubscriptionId();
        log.error("Event listener subscription={} caught processing error:", subscriptionId, throwable);
    }

    private void onPositionOnTopicPartition(KafkaSubscriptionMetadata metadata) {
        Collection<TopicPartition> topicPartitions;
        String topic = metadata.getTopic();
        if (CollectionUtils.isNotEmpty(metadata.getPartition())) {
            topicPartitions = metadata.getPartition()
                    .stream()
                    .map(partition -> new TopicPartition(topic, partition))
                    .toList();
        } else {
            topicPartitions = Collections.singleton(new TopicPartition(topic, 0));
        }
        kafkaReceiver.doOnConsumer(kafkaConsumer -> {
            topicPartitions.forEach(kafkaConsumer::position);
            return kafkaConsumer;
        }).then().block();
    }

    private Consumer<String, Object> closeConsumer(Consumer<String, Object> kafkaConsumer) {
        kafkaConsumer.close();
        return kafkaConsumer;
    }

    @Override
    public void close() throws Exception {
        kafkaReceiver.doOnConsumer(this::closeConsumer)
                .then()
                .block();
        closed = true;
    }
}
