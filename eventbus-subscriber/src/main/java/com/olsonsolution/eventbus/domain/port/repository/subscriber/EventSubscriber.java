package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

import java.util.Collection;

public interface EventSubscriber<C> extends Participant {

    Collection<EventDestination> getSubscribedDestinations();

    void subscribe(EventDestination destination);

    void unsubscribe(EventDestination destination);

    void receive();

    void stopReceiving();

}
