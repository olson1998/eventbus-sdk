package com.olsonsolution.eventbus.domain.service.publisher.kafka;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.schemas.asyncapi.AsyncAPISchema;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olsonsolution.eventbus.domain.model.exception.*;
import com.olsonsolution.eventbus.domain.model.kafka.KafkaAcknowledgment;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.KafkaPublisherSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class KafkaEventDispatcher<C, S extends KafkaPublisherSubscription> implements EventDispatcher<C, S> {

    private KafkaSender<String, C> kafkaSender;

    private Collection<Channel> channels;

    @Getter
    private final S subscription;

    private final EventMapper<C> eventMapper;

    private final KafkaFactory kafkaFactory;

    private final ObjectMapper objectMapper;

    @Override
    public void register() {
        if (kafkaSender == null) {
            subscription.register();
            SubscriptionMetadata metadata = subscription.getMetadata();
            channels = getChannels();
            kafkaSender = kafkaFactory.fabricateSender(
                    subscription.getSubscriptionId(),
                    metadata.getApiDocs(),
                    eventMapper
            );
        }
    }

    @Override
    public void unregister() {
        if (kafkaSender != null) {
            kafkaSender.close();
            subscription.unregister();
        }
    }

    protected Mono<List<EventAcknowledgment>> dispatchSenderRecords(List<SenderRecord<String, C, UUID>> senderRecords) {
        return kafkaSender.send(Flux.fromIterable(senderRecords))
                .collectList()
                .flatMap(senderResults ->
                        Mono.fromCallable(() -> processSendResults(senderResults, senderRecords))
                );
    }

    protected Mono<List<SenderRecord<String, C, UUID>>> prepareSenderRecords(EventMessage<C> message) {
        return Flux.fromStream(() -> CollectionUtils.emptyIfNull(channels).stream())
                .flatMap(channel -> Mono.fromCallable(() -> listSenderRecords(channel, message))
                        .flatMapMany(Flux::fromIterable))
                .collectList();
    }

    private List<SenderRecord<String, C, UUID>> listSenderRecords(Channel channel, EventMessage<C> message)
            throws EventDispatchException {
        verifySubscription();
        List<TopicPartition> topicPartitions = listTopicPartitions();
        return Optional.ofNullable(channel.getMessages())
                .flatMap(messages -> Optional.ofNullable(messages.get(eventMapper.getMessageName())))
                .filter(AsyncAPISchema.class::isInstance)
                .map(AsyncAPISchema.class::cast)
                .stream()
                .flatMap(schema -> mapToSenderRecords(schema, message, topicPartitions))
                .toList();
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
            KafkaPartialSendRecordDispatchException e = new KafkaPartialSendRecordDispatchException(
                    subscription.getDestination(),
                    collectedAcknowledgments,
                    collectedDispatchExceptions
            );
            collectedDispatchExceptions.forEach(e::addSuppressed);
            throw e;
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
                    } else if (senderResult.exception() != null) {
                        dispatchExceptions.add(new KafkaSendRecordDispatchException(
                                senderResult.exception(),
                                senderRecord,
                                subscription.getDestination(),
                                subscription.getMetadata()
                        ));
                    }
                }, () -> dispatchExceptions.add(new NoMatchingCorrelationException(senderRecord)));
    }

    private Stream<SenderRecord<String, C, UUID>> mapToSenderRecords(AsyncAPISchema schema,
                                                                     EventMessage<C> message,
                                                                     Collection<TopicPartition> topicPartitions) {
        return topicPartitions.stream()
                .map(topicPartition -> mapToSenderRecord(
                        message,
                        topicPartition.topic(),
                        topicPartition.partition(),
                        schema
                ));
    }

    private SenderRecord<String, C, UUID> mapToSenderRecord(EventMessage<C> message,
                                                            String topic,
                                                            Integer partition,
                                                            AsyncAPISchema schema) {
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

    private Collection<Channel> getChannels() {
        return Optional.ofNullable(subscription)
                .flatMap(sub -> Optional.ofNullable(sub.getMetadata()))
                .flatMap(metadata -> Optional.ofNullable(metadata.getApiDocs()))
                .map(apiDocs -> MapUtils.emptyIfNull(apiDocs.getChannels()))
                .map(Map::values)
                .stream()
                .flatMap(Collection::stream)
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast)
                .toList();
    }

    private List<TopicPartition> listTopicPartitions() {
        return CollectionUtils.emptyIfNull(channels)
                .stream()
                .flatMap(this::streamTopicPartitionsForChannel)
                .toList();
    }

    private Stream<TopicPartition> streamTopicPartitionsForChannel(Channel channel) {
        return Optional.ofNullable(channel.getBindings())
                .flatMap(bound -> Optional.ofNullable(bound.get("kafka")))
                .filter(KafkaChannelBinding.class::isInstance)
                .map(KafkaChannelBinding.class::cast)
                .stream()
                .flatMap(this::streamTopicPartitionsForKafkaChannel);
    }

    private Stream<TopicPartition> streamTopicPartitionsForKafkaChannel(KafkaChannelBinding kafkaChannelBinding) {
        Map<String, Object> extensions = kafkaChannelBinding.getExtensionFields();
        String topic = kafkaChannelBinding.getTopic();
        if (extensions != null && extensions.containsKey("x-assigned-partition")) {
            Object extensionValue = extensions.get("x-assigned-partition");
            if (extensionValue instanceof Integer partition) {
                return Stream.of(new TopicPartition(topic, partition));
            } else {
                return Stream.empty();
            }
        } else {
            return Stream.of(new TopicPartition(topic, 0));
        }
    }

    @Override
    public void close() throws Exception {
        kafkaSender.close();
    }
}
