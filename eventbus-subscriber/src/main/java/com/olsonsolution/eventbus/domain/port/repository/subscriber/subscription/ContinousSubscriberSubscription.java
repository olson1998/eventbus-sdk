package com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription;

import java.time.Duration;

public interface ContinousSubscriberSubscription extends SubscriberSubscription {

    boolean isStopped();

    Duration getReceiveInterval();

    void stop();

}
