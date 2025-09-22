package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.time.Duration;
import java.util.Collection;

public interface EventListener<S extends SubscriberSubscription<M>, M extends SubscriptionMetadata> {

    S getSubscription();

    Collection<? extends EventMessage<?>> receive(Duration listeningDuration);

}
