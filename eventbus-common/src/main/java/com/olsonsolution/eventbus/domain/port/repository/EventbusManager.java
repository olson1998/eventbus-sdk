package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface EventbusManager<M extends SubscriptionMetadata> {

    M registerPublisher(UUID subscriptionId, EventDestination destination);

    UUID registerSubscriber();

    M renewPublisherSubscription(UUID subscriptionId);

    void renewSubscriberSubscription(UUID subscriptionId);

    void unreqisterSubscription(UUID subscriptionId);

    M subscribeDestination(UUID subscriptionId, EventDestination destination);

}
