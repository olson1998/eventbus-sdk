package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.BatchingPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface BatchingEventDispatcher<C, M extends SubscriptionMetadata>
        extends EventDispatcher<C, BatchingPublisherSubscription<M>, M> {

    CompletableFuture<Collection<? extends EventAcknowledgment>> publishBulk(Collection<EventMessage<C>> messages);

}
