package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventDispatcher<C, S extends PublisherSubscription<M>, M extends SubscriptionMetadata>
        extends AutoCloseable {

    S getSubscription();

    Mono<List<EventAcknowledgment>> dispatch(EventMessage<C> message);

}
