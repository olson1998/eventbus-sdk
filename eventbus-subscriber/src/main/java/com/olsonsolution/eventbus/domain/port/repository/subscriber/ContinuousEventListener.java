package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.ContinousSubscriberSubscription;

public interface ContinuousEventListener<C> extends EventListener<C, ContinousSubscriberSubscription> {

}
