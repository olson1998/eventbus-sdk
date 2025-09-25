package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.util.concurrent.CompletableFuture;

public class OnDemandKafkaEventListener extends KafkaEventListener<OnDemandKafkaSubscriberSubscription> {

    public OnDemandKafkaEventListener(OnDemandKafkaSubscriberSubscription subscription,
                                      KafkaReceiver<String, Object> kafkaReceiver) {
        super(subscription, kafkaReceiver);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor eventProcessor) {
        return consume()
                .doOnNext(eventProcessor::onEvent)
                .collectList()
                .flatMap(events -> Mono.<Void>empty())
                .toFuture();
    }
}
