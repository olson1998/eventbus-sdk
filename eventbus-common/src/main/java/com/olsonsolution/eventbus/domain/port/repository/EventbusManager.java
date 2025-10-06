package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface EventbusManager {

    SubscriptionMetadata registerPublisher(EventChannel channel);

    UUID getChannelId(EventChannel channel);

    UUID registerSubscription();

    SubscriptionMetadata renewPublisherSubscription(UUID subscriptionId);

    void renewSubscriberSubscription(UUID subscriptionId);

    void unregisterSubscription(UUID subscriptionId);

    SubscriptionMetadata subscribeChannel(UUID subscriptionId, EventChannel destination);

}
