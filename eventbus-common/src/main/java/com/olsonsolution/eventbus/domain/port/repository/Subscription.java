package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface Subscription {

    SubscriptionMetadata getMetadata();

    void renew();

}
