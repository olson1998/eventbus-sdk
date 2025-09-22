package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Map.entry;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventSubscriber<P extends EventProcessor> implements EventSubscriber<P> {

    private final P eventProcessor;

    private final KafkaReceiver<String, Object> kafkaReceiver;

    @Override
    public void receive() {
        kafkaReceiver.receive()
                .map(this::mapToEventMessage)
                .doOnNext(eventProcessor::onEvent)
                .collectList()
                .toFuture();
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

}
