package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.BatchingPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface BatchingEventPublisher<C, D extends EventDestination>
        extends EventPublisher<C, D, BatchingPublisherSubscription<D>> {

    CompletableFuture<Collection<? extends EventAcknowledgment>> publishBulk(Collection<EventMessage<C>> messages);

}
