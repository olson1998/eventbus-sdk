package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.concurrent.CompletableFuture;

public interface EventDispatcher<C, S extends PublisherSubscription<M>, M extends SubscriptionMetadata> {

    S getSubscription();

    void subscribe();

    CompletableFuture<EventAcknowledgment> publish(EventMessage<C> message);

}
