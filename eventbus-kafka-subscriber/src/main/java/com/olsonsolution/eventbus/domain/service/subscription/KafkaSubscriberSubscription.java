package com.olsonsolution.eventbus.domain.service.subscription;

import com.olsonsolution.eventbus.domain.port.repository.subscriber.subscription.SubscriberSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class KafkaSubscriberSubscription implements SubscriberSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private UUID subscriptionId;

    @Getter
    private final Collection<? extends EventDestination> subscribedDestinations = new HashSet<>();

    @Override
    public KafkaSubscriptionMetadata getMetadata() {
        return null;
    }

    @Override
    public void renew() {

    }
}
