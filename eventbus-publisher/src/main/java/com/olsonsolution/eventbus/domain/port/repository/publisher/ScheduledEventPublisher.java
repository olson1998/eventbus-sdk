package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface ScheduledEventPublisher<C, D extends EventDestination>
        extends EventPublisher<C, D, ScheduledPublisherSubscription<D>> {


}
