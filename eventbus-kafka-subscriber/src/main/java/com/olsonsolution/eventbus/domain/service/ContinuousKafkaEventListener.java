package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
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

    public ContinuousKafkaEventListener(ContinuousKafkaSubscriberSubscription subscription,
                                        EventMapper<C> eventMapper,
                                        KafkaFactory kafkaFactory) {
        super(subscription, eventMapper, kafkaFactory);
    }

    @Override
    public CompletableFuture<Void> receive(EventProcessor<C> eventProcessor) {
        ContinuousKafkaSubscriberSubscription sub = getSubscription();
        Duration consumeInterval = sub.getReceiveInterval();
        stopped.set(sub.isStopped());
        return Flux.interval(Duration.ZERO, consumeInterval)
                .takeWhile(t -> isClosed())
                .concatMap(t -> Mono.fromRunnable(() -> consumeOnIntervals(eventProcessor)))
                .then()
                .toFuture();
    }

    private void consumeOnIntervals(EventProcessor<C> eventProcessor) {
        if (!stopped.get()) {
            consumeAndProcess(eventProcessor);
        }
    }

}
