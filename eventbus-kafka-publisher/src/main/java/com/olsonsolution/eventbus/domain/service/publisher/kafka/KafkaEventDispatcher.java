package com.olsonsolution.eventbus.domain.service.publisher.kafka;

import com.olsonsolution.eventbus.domain.model.exception.*;
import com.olsonsolution.eventbus.domain.model.kafka.KafkaAcknowledgment;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.KafkaPublisherSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventDispatcher<C, S extends KafkaPublisherSubscription>
        implements EventDispatcher<C, S, KafkaSubscriptionMetadata> {

    @Getter
    private final S subscription;

    private final KafkaSender<String, C> kafkaSender;

    protected Mono<List<EventAcknowledgment>> dispatchSenderRecords(List<SenderRecord<String, C, UUID>> senderRecords) {
        return kafkaSender.send(Flux.fromIterable(senderRecords))
                .collectList()
                .flatMap(senderResults ->
                        Mono.fromCallable(() -> processSendResults(senderResults, senderRecords))
                );
    }

    protected Mono<List<SenderRecord<String, C, UUID>>> prepareSenderRecords(EventMessage<C> message) {
        return Mono.fromCallable(() -> listSenderRecords(message));
    }

    private List<SenderRecord<String, C, UUID>> listSenderRecords(EventMessage<C> message)
            throws EventDispatchException {
        verifySubscription();
        KafkaSubscriptionMetadata sub = subscription.getMetadata();
        String topic = sub.getTopic();
        if (CollectionUtils.isNotEmpty(sub.getPartition())) {
            return sub.getPartition().stream()
                    .map(partition -> mapToSenderRecord(message, topic, partition))
                    .sorted(Comparator.comparing(ProducerRecord::partition))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            return Collections.singletonList(mapToSenderRecord(message, topic, null));
        }
    }

    protected List<EventAcknowledgment> processSendResults(Collection<SenderResult<UUID>> senderResults,
                                                           List<SenderRecord<String, C, UUID>> senderRecords)
            throws KafkaPartialSendRecordDispatchException {
        Stream.Builder<EventAcknowledgment> acknowledgments = Stream.builder();
        Stream.Builder<EventDispatchException> dispatchExceptions = Stream.builder();
        senderRecords.forEach(senderRecord ->
                acknowledgeResponse(senderRecord, senderResults, acknowledgments, dispatchExceptions));
        List<EventAcknowledgment> collectedAcknowledgments = acknowledgments.build().toList();
        List<EventDispatchException> collectedDispatchExceptions = dispatchExceptions.build().toList();
        if (CollectionUtils.isNotEmpty(collectedDispatchExceptions)) {
            throw new KafkaPartialSendRecordDispatchException(
                    subscription.getDestination(),
                    collectedAcknowledgments,
                    collectedDispatchExceptions
            );
        }
        return collectedAcknowledgments;
    }

    private void verifySubscription() throws EventDispatchException {
        if (subscription.getMetadata() == null) {
            throw new SubscriptionNotCreatedException(subscription.getDestination());
        } else {
            ZonedDateTime expireAt = subscription.getMetadata().getExpireAt();
            if (expireAt.isBefore(ZonedDateTime.now())) {
                throw new PublisherSubscriptionExpiredException(subscription);
            }
        }
    }

    private void acknowledgeResponse(SenderRecord<String, C, UUID> senderRecord,
                                     Collection<SenderResult<UUID>> senderResults,
                                     Stream.Builder<EventAcknowledgment> acknowledgments,
                                     Stream.Builder<EventDispatchException> dispatchExceptions) {
        senderResults.stream()
                .filter(senderResult -> senderResult.correlationMetadata()
                        .equals(senderRecord.correlationMetadata()))
                .findFirst()
                .ifPresentOrElse(senderResult -> {
                    if (senderResult.recordMetadata() != null) {
                        acknowledgments.add(new KafkaAcknowledgment(senderResult.recordMetadata()));
                    }
                    dispatchExceptions.add(new KafkaSendRecordDispatchException(
                            senderResult.exception(),
                            senderRecord,
                            subscription.getDestination(),
                            subscription.getMetadata()
                    ));
                }, () -> dispatchExceptions.add(new NoMatchingCorrelationException(senderRecord)));
    }

    private SenderRecord<String, C, UUID> mapToSenderRecord(EventMessage<C> message,
                                                            String topic,
                                                            Integer partition) {
        Iterable<Header> headers = MapUtils.emptyIfNull(message.getHeaders())
                .entrySet()
                .stream()
                .map(this::mapToHeader)
                .toList();
        ProducerRecord<String, C> producerRecord = new ProducerRecord<>(
                topic,
                partition,
                message.getTimestamp().toInstant().toEpochMilli(),
                null,
                message.getContent(),
                headers
        );
        return SenderRecord.create(producerRecord, UUID.randomUUID());
    }

    private Header mapToHeader(Map.Entry<String, Object> headerEntry) {
        return new RecordHeader(
                headerEntry.getKey(),
                String.valueOf(headerEntry.getValue()).getBytes()
        );
    }

    @Override
    public void close() throws Exception {
        kafkaSender.close();
    }
}
