package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import com.olsonsolution.eventbus.domain.port.repository.Subscription;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

import java.util.UUID;

public interface PublisherSubscription<M extends SubscriptionMetadata> extends Subscription<M> {

    UUID getSubscriptionId();

    EventDestination getDestination();

    void register();

    void unregister();

}
