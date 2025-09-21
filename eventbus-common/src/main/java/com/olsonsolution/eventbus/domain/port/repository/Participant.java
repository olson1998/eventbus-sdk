package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface Participant<S extends Subscription<M>, M extends SubscriptionMetadata> {

    S getSubscription();

    void subscribe();

}
