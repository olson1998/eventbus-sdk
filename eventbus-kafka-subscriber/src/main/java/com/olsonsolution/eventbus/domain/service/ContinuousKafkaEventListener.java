package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.ConsumedCorruptedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ContinuousKafkaEventListener<C> extends KafkaEventListener<C, ContinuousKafkaSubscriberSubscription> {

    private final ConcurrentMap<UUID, Disposable> consumerSubscriptions = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, KafkaReceiver<String, MappingResult<C>>> subscriptionReceivers =
            new ConcurrentHashMap<>();

    public ContinuousKafkaEventListener(Duration maxPollInterval,
                                        EventMapper<C> eventMapper,
                                        KafkaFactory kafkaFactory,
                                        ContinuousKafkaSubscriberSubscription subscription) {
        super(maxPollInterval, eventMapper, kafkaFactory, subscription);
    }

    @Override
    public void subscribe(EventDestination destination) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = getSubscription().subscribe(destination);
        UUID subscriptionId = metadata.getId();
        subscriptionReceivers.computeIfAbsent(
                subscriptionId,
                sid -> kafkaFactory.fabricateReceiver(
                        maxPollInterval,
                        sid,
                        destination,
                        metadata.getApiDocs(),
                        eventMapper
                )
        );
        getSubscription().getSubscribedDestinations().put(destination, metadata);
    }

    @Override
    public void unsubscribe(EventDestination destination) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = getSubscription().getSubscribedDestinations().get(destination);
        if (metadata == null) {
            return;
        }
        UUID subscriptionId = metadata.getId();
        if (subscriptionReceivers.containsKey(subscriptionId)) {
            KafkaReceiver<String, MappingResult<C>> kafkaReceiver = subscriptionReceivers.get(subscriptionId);
            kafkaReceiver.doOnConsumer(consumer -> {
                getSubscription().getSubscribedDestinations().remove(destination);
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
        disposeSubscriptions();
        listening = false;
    }

    private void processEvent(ReceiverRecord<String, MappingResult<C>> receiverRecord,
                              EventProcessor<C> eventProcessor) {
        KafkaEventMessage<C> kafkaEventMessage = mapToKafkaEventMessage(receiverRecord);
        try {
            processKafkaEvent(kafkaEventMessage, eventProcessor);
        } catch (Exception e) {
            eventProcessor.onError(e);
        }
        receiverRecord.receiverOffset().acknowledge();
    }

    private void disposeSubscriptions() {
        for (Disposable kafkaConsumerSubscription : consumerSubscriptions.values()) {
            if (kafkaConsumerSubscription.isDisposed()) {
                kafkaConsumerSubscription.dispose();
            }
        }
        consumerSubscriptions.clear();
    }

    private void processKafkaEvent(KafkaEventMessage<C> kafkaEventMessage, EventProcessor<C> eventProcessor) {
        if (kafkaEventMessage instanceof ConsumedCorruptedKafkaEventMessage<C> corruptedKafkaEventMessage) {
            eventProcessor.onCorruptedEvent(corruptedKafkaEventMessage);
        } else {
            eventProcessor.onEvent(kafkaEventMessage);
        }
    }

    private Disposable subscribeToReceiver(KafkaReceiver<String, MappingResult<C>> kafkaReceiver,
                                           EventProcessor<C> eventProcessor) {
        return kafkaReceiver.receive()
                .subscribe(record -> processEvent(record, eventProcessor));
    }

    @Override
    public void close() {
        stopListening();
        closed = true;
    }
}
