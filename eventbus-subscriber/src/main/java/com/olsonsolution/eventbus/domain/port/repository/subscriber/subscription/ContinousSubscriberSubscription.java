package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.time.Duration;

public interface ContinousSubscriberSubscription<M extends SubscriptionMetadata> extends SubscriberSubscription<M> {

    boolean isStopped();

    Duration getReceiveInterval();

    void stop();

}
