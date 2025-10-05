package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.TopicPartition;
import reactor.kafka.receiver.ReceiverPartition;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class OnDemandKafkaEventListener<C> extends KafkaEventListener<C, OnDemandKafkaSubscriberSubscription> {

    public OnDemandKafkaEventListener(OnDemandKafkaSubscriberSubscription subscription,
                                      EventMapper<C> eventMapper,
                                      KafkaFactory kafkaFactory) {
        super(subscription, eventMapper, kafkaFactory);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor<C> eventProcessor) {
        consumeAndProcess(eventProcessor);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected void onReceiverPartitionsAssigned(Collection<ReceiverPartition> receiverPartitions) {
        Collection<TopicPartition> assignment =
                CollectionUtils.collect(receiverPartitions, ReceiverPartition::topicPartition);
        log.info("Refreshed assignment: {}", assignment);
    }
}
