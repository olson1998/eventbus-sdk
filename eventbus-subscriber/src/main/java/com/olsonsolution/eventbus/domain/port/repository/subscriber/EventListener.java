package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.concurrent.CompletableFuture;

public interface EventListener<S extends SubscriberSubscription<M>, M extends SubscriptionMetadata>
        extends AutoCloseable {

    boolean isClosed();

    S getSubscription();

    CompletableFuture<Void> receive(EventProcessor eventProcessor);

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

}
