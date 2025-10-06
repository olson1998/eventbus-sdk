package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface EventListener<C, S extends SubscriberSubscription> extends AutoCloseable {

    boolean isClosed();

    boolean isListening();

    S getSubscription();

    void listen(EventProcessor<C> eventProcessor);

    void stopListening();

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

}
