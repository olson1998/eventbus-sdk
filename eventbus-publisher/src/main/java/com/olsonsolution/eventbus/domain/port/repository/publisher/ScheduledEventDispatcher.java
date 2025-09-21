package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.ScheduledPublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface ScheduledEventDispatcher<C, M extends SubscriptionMetadata> extends EventDispatcher<C, ScheduledPublisherSubscription<M>, M> {


}
