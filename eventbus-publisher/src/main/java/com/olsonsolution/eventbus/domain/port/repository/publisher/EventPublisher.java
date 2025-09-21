package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

import java.util.concurrent.CompletableFuture;

public interface EventPublisher<C, D extends EventDestination, S extends PublisherSubscription<D>>
        extends Participant<S> {

    /**
     * Publishes an event message to its intended destination.
     *
     * @param message the event message to be published, containing content, headers, and a timestamp
     * @return a CompletableFuture representing the acknowledgment of the event publication,
     * containing the acknowledgment timestamp upon successful completion
     */
    CompletableFuture<EventAcknowledgment> publish(EventMessage<C> message);

}
