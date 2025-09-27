package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;

import java.util.concurrent.CompletableFuture;

public class OnDemandKafkaEventListener<C> extends KafkaEventListener<C, OnDemandKafkaSubscriberSubscription> {

    public OnDemandKafkaEventListener(Class<C> contentClass,
                                      OnDemandKafkaSubscriberSubscription subscription,
                                      KafkaFactory kafkaFactory) {
        super(contentClass, subscription, kafkaFactory);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor<C> eventProcessor) {
        return consume()
                .flatMap(eventMessage -> processEventAndEmitStatus(eventMessage, eventProcessor))
                .collectList()
                .then()
                .toFuture();
    }
}
