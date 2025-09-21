package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.concurrent.CompletableFuture;

public interface EventPublisher<C, S extends PublisherSubscription<M>, M extends SubscriptionMetadata>
        extends Participant<S, M> {

    /**
     * Publishes an event message to its intended destination.
     *
     * @param message the event message to be published, containing content, headers, and a timestamp
     * @return a CompletableFuture representing the acknowledgment of the event publication,
     * containing the acknowledgment timestamp upon successful completion
     */
    CompletableFuture<EventAcknowledgment> publish(EventMessage<C> message);

}
