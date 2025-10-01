package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;

public interface ScheduledEventDispatcher<C> extends EventDispatcher<C, ScheduledPublisherSubscription> {


}
