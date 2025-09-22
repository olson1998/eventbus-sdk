package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface EventbusManager<M extends SubscriptionMetadata> {

    M getSubscriptionMetadata(UUID subscriptionId);

    M renewSubscription(UUID subscriptionId);

    M registerSubscription(EventDestination destination);

    void unreqisterSubscription(UUID subscriptionId);

}
