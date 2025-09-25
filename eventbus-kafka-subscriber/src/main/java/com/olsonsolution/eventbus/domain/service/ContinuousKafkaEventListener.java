package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ContinuousKafkaEventListener extends KafkaEventListener<ContinuousKafkaSubscriberSubscription> {

    private final AtomicBoolean stopped = new AtomicBoolean(true);

    public ContinuousKafkaEventListener(ContinuousKafkaSubscriberSubscription subscription,
                                        KafkaReceiver<String, Object> kafkaReceiver) {
        super(subscription, kafkaReceiver);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor eventProcessor) {
        ContinuousKafkaSubscriberSubscription sub = getSubscription();
        Duration consumeInterval = sub.getReceiveInterval();
        stopped.set(sub.isStopped());
        return Flux.interval(Duration.ZERO, consumeInterval)
                .takeWhile(t -> isClosed())
                .concatMap(t -> consumeOnIntervals(eventProcessor))
                .then()
                .toFuture();
    }

    private Mono<Void> consumeOnIntervals(EventProcessor eventProcessor) {
        if (stopped.get()) {
            return Mono.empty();
        } else {
            return consume().flatMap(eventMessage -> processEventAndEmitStatus(
                    eventMessage,
                    eventProcessor
            )).then();
        }
    }

}
