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
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.olsonsolution.eventbus.domain.model.MemberTypes.ALL;

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
        return getSubscription().getSubscriptionChannels()
                .keySet()
                .stream()
                .anyMatch(this::isListeningToChannel);
    }

    @Override
    public void subscribe(EventChannel channel) {
        assertIsNotClosed();
        ContinuousKafkaSubscriberSubscription s = getSubscription();
        UUID channelId = s.findIdByChannel(channel).orElse(null);
        SubscriptionMetadata metadata;
        if (channelId != null) {
            disposeSubscription(channelId);
            channelSubscriptions.remove(channelId);
            channelReceivers.remove(channelId);
        }
        metadata = s.subscribe(channel);
        channelId = metadata.getChannelId();
        UUID subId = s.getSubscriptionId();
        String groupId = "channel-" + channelId + "-subscriber";
        if (areAllSubscribersAllType(channelId)) {
            groupId = "subscription-" + subId + "-channel-subscriber";
        }
        KafkaReceiver<String, MappingResult<C>> kafkaReceiver = kafkaFactory.fabricateReceiver(
                maxPollInterval,
                subId,
                groupId,
                metadata.getApiDocs(),
                eventMapper
        );
        channelReceivers.put(channelId, kafkaReceiver);
    }

    @Override
    public void unsubscribe(EventChannel channel) {
        assertIsNotClosed();
        ContinuousKafkaSubscriberSubscription s = getSubscription();
        UUID channelId = s.findIdByChannel(channel).orElse(null);
        UUID subId = s.getSubscriptionId();
        if (channelId == null) {
            log.warn("Subscription: '{}' Channel {} is not subscribed", subId, channel);
            return;
        }
        SubscriptionMetadata metadata = s.findIdByChannel(channel).flatMap(s::findMetadataByChannel).orElse(null);
        if (metadata == null) {
            log.warn("Subscription: '{}' Channel {} metadata not found", subId, channel);
            return;
        }
        disposeSubscription(channelId);
        channelSubscriptions.remove(channelId);
        channelReceivers.remove(channelId);
        metadata = s.unsubscribe(channel);
        if (metadata != null) {
            log.info("Subscription: '{}' resubscribing after leaving channel: {}", subId, channel);
            subscribe(channel);
        }
    }

    @Override
    public void listen(EventProcessor<C> eventProcessor) {
        assertIsNotClosed();
        for (UUID channelId : getSubscription().getSubscriptionChannels().keySet()) {
            listen(channelId, eventProcessor);
        }
    }

    @Override
    public void stopListening() {
        assertIsNotClosed();
        disposeSubscriptions();
    }

    private void listen(UUID channelId, EventProcessor<C> eventProcessor) {
        KafkaReceiver<String, MappingResult<C>> kafkaReceiver = channelReceivers.get(channelId);
        if (kafkaReceiver != null) {
            channelSubscriptions.computeIfAbsent(
                    channelId,
                    cid -> subscribeToReceiver(kafkaReceiver, eventProcessor)
            );
        } else {
            log.warn(
                    "Subscription: '{}' Channel {} is receiver not created yet",
                    getSubscription().getSubscriptionId(), channelId
            );
        }
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
        if (!channelSubscription.isDisposed()) {
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

    private boolean isListeningToChannel(UUID channelId) {
        return !channelSubscriptions.containsKey(channelId) || !channelSubscriptions.get(channelId).isDisposed();
    }

    private boolean areAllSubscribersAllType(UUID channelId) {
        if (getSubscription().getSubscriptionChannels().containsKey(channelId)) {
            return getSubscription().getSubscriptionChannels().get(channelId)
                    .stream()
                    .allMatch(channel -> channel.getSubscriber().getType().isEqualTo(ALL));
        } else {
            return false;
        }
    }

    @Override
    public void close() {
        stopListening();
        closed = true;
    }
}
