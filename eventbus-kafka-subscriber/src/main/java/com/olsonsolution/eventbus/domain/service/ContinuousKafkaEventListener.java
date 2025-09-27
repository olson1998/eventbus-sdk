package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ContinuousKafkaEventListener<C> extends KafkaEventListener<C, ContinuousKafkaSubscriberSubscription> {

    private final AtomicBoolean stopped = new AtomicBoolean(true);

    public ContinuousKafkaEventListener(Class<C> contentClass,
                                        ContinuousKafkaSubscriberSubscription subscription,
                                        KafkaFactory kafkaFactory) {
        super(contentClass, subscription, kafkaFactory);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor<C> eventProcessor) {
        ContinuousKafkaSubscriberSubscription sub = getSubscription();
        Duration consumeInterval = sub.getReceiveInterval();
        stopped.set(sub.isStopped());
        return Flux.interval(Duration.ZERO, consumeInterval)
                .takeWhile(t -> isClosed())
                .concatMap(t -> consumeOnIntervals(eventProcessor))
                .then()
                .toFuture();
    }

    private Mono<Void> consumeOnIntervals(EventProcessor<C> eventProcessor) {
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
