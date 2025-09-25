package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface OnDemandSubscriberSubscription <M extends SubscriptionMetadata> extends Subscription<M> {
}
