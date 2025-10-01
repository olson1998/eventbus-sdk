package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface Subscription {

    UUID getSubscriptionId();

    void renew();

    void register();

    void unregister();

}
