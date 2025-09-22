package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface ContinousSubscriberSubscription<M extends SubscriptionMetadata> extends SubscriberSubscription<M> {
}
