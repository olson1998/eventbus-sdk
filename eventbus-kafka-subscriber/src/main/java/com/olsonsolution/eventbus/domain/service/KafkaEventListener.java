package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.ConsumedCorruptedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.model.kafka.ConsumedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.util.Map.entry;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventListener<C, S extends SubscriberSubscription> implements EventListener<C, S> {

    @Getter
    protected boolean closed;

    protected final Duration maxPollInterval;

    protected final EventMapper<C> eventMapper;

    protected final KafkaFactory kafkaFactory;

    @Getter
    private final S subscription;

    protected void assertIsNotClosed() {
        if (closed) {
            throw new IllegalStateException("Event listener is closed");
        }
    }

    protected KafkaEventMessage<C> mapToKafkaEventMessage(ReceiverRecord<String, MappingResult<C>> receiverRecord) {
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

}