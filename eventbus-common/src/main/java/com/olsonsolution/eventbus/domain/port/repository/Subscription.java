package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface Subscription<M extends SubscriptionMetadata> {

    M getMetadata();

    void renew();

}
