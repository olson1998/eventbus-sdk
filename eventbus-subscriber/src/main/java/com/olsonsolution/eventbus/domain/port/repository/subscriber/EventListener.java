package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.util.concurrent.CompletableFuture;

public interface EventListener<C, S extends SubscriberSubscription>
        extends AutoCloseable {

    boolean isClosed();

    Class<C> getContentClass();

    S getSubscription();

    CompletableFuture<Void> receive(EventProcessor<C> eventProcessor);

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

}
