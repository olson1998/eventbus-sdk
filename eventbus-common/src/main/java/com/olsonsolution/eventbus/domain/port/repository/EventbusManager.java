package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface EventbusManager {

    SubscriptionMetadata registerPublisher(UUID subscriptionId, EventDestination destination);

    UUID registerSubscriber();

    SubscriptionMetadata renewPublisherSubscription(UUID subscriptionId);

    void renewSubscriberSubscription(UUID subscriptionId);

    void unregisterSubscription(UUID subscriptionId);

    SubscriptionMetadata subscribeDestination(UUID subscriptionId, EventDestination destination);

}
