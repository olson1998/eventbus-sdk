package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface EventSubscriber<C> extends Participant {

    Collection<EventDestination> getSubscribedDestinations();

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

    CompletableFuture<Void> receive();

}
