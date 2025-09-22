package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.ContinousSubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.concurrent.ScheduledFuture;

public interface ContinousEventListener<M extends SubscriptionMetadata>
        extends EventListener<ContinousSubscriberSubscription<M>, M> {

    ScheduledFuture<?> continousReceive();

}
