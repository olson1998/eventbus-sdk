package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.kafka.ConsumedCorruptedKafkaEventMessage;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

    private final ConcurrentMap<UUID, Disposable> channelSubscriptions = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, KafkaReceiver<String, MappingResult<C>>> channelReceivers =
            new ConcurrentHashMap<>();

    public ContinuousKafkaEventListener(Duration maxPollInterval,
                                        EventMapper<C> eventMapper,
                                        KafkaFactory kafkaFactory,
                                        ContinuousKafkaSubscriberSubscription subscription) {
        super(maxPollInterval, eventMapper, kafkaFactory, subscription);
    }

    @Override
    public boolean isListening() {

    }

    @Override
    public void subscribe(EventChannel channel) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = getSubscription().subscribe(channel);
        subscribe(channel, metadata);
    }

    @Override
    public void unsubscribe(EventChannel channel) {
        assertIsNotClosed();
        SubscriptionMetadata metadata = getSubscription().getSubscribedDestinations().get(channel);
        if (metadata == null) {
            return;
        }
        UUID channelId = metadata.getChannelId();
        disposeSubscription(channelId);
        channelSubscriptions.remove(channelId);
        channelReceivers.remove(channelId);
        getSubscription().getSubscribedDestinations().remove(channel);
    }

    @Override
    public void listen(EventProcessor<C> eventProcessor) {
        assertIsNotClosed();
        for (Map.Entry<EventChannel, SubscriptionMetadata> channelMetadata :
                getSubscription().getSubscribedDestinations().entrySet()) {
            SubscriptionMetadata metadata = channelMetadata.getValue();
            UUID channelId = metadata.getChannelId();
            boolean isSubscribed = channelSubscriptions.containsKey(channelId);
            KafkaReceiver<String, MappingResult<C>> kafkaReceiver = channelReceivers.get(channelId);
            if (!isSubscribed && kafkaReceiver != null) {
                Disposable channelSubscription = subscribeToReceiver(kafkaReceiver, eventProcessor);
                channelSubscriptions.put(channelId, channelSubscription);
            } else if (!isSubscribed) {
                subscribe(channelMetadata.getKey(), channelMetadata.getValue());
                kafkaReceiver = kafkaFactory.fabricateReceiver(
                        maxPollInterval,
                        metadata.getId(),
                        channelMetadata.getKey(),
                        metadata.getApiDocs(),
                        eventMapper
                );
                channelReceivers.put(channelId, kafkaReceiver);
                Disposable channelSubscription = subscribeToReceiver(kafkaReceiver, eventProcessor);
                channelSubscriptions.put(channelId, channelSubscription);
            }
        }
    }

    @Override
    public void stopListening() {
        assertIsNotClosed();
        disposeSubscriptions();
    }

    private void subscribe(EventChannel channel, SubscriptionMetadata metadata) {
        UUID channelId = metadata.getChannelId();
        KafkaReceiver<String, MappingResult<C>> kafkaReceiver;
        if (channelReceivers.containsKey(channelId)) {
            disposeSubscription(channelId);
        }
        kafkaReceiver = kafkaFactory.fabricateReceiver(
                maxPollInterval,
                channelId,
                channel,
                metadata.getApiDocs(),
                eventMapper
        );
        channelReceivers.put(channelId, kafkaReceiver);
        getSubscription().getSubscribedDestinations().put(channel, metadata);
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
        for (Map.Entry<UUID, Disposable> channelSubscription : channelSubscriptions.entrySet()) {
            disposeSubscription(channelSubscription.getValue());
            channelSubscriptions.remove(channelSubscription.getKey());
            channelReceivers.remove(channelSubscription.getKey());
        }
    }

    private void disposeSubscription(UUID channelId) {
        if (channelSubscriptions.containsKey(channelId)) {
            disposeSubscription(channelSubscriptions.get(channelId));
            channelSubscriptions.remove(channelId);
            channelReceivers.remove(channelId);
        }
    }

    private void disposeSubscription(Disposable channelSubscription) {
        if (channelSubscription.isDisposed()) {
            channelSubscription.dispose();
        }
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

    private boolean isListeningToChannel(Map.Entry<EventChannel, SubscriptionMetadata> channelMetadata) {
        UUID channelId = channelMetadata.getValue().getChannelId();
        return !channelSubscriptions.containsKey(channelId) || !channelSubscriptions.get(channelId).isDisposed();
    }

    @Override
    public void close() {
        stopListening();
        closed = true;
    }
}
