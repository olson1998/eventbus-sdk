package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventbusManager;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class TestEventbusManager implements EventbusManager {

    @Override
    public SubscriptionMetadata registerPublisher(EventChannel channel) {
        return null;
    }

    @Override
    public UUID getChannelId(EventChannel channel) {
        return null;
    }

    @Override
    public UUID registerSubscription() {
        return null;
    }

    @Override
    public SubscriptionMetadata renewPublisherSubscription(UUID subscriptionId) {
        return null;
    }

    @Override
    public void renewSubscriberSubscription(UUID subscriptionId) {

    }

    @Override
    public void unregisterSubscription(UUID subscriptionId) {

    }

    @Override
    public SubscriptionMetadata subscribeChannel(UUID subscriptionId, EventChannel destination) {
        return null;
    }
}