package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface EventSubscriber<P extends EventProcessor> extends Participant {

    Collection<EventDestination> getSubscribedDestinations();

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

    CompletableFuture<Void> receive();

}
