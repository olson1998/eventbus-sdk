package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;

import java.util.Collection;

public interface EventSubscriber<C> extends Participant {

    Collection<EventChannel> getSubscribedDestinations();

    void subscribe(EventChannel destination);

    void unsubscribe(EventChannel destination);

    void receive();

    void stopReceiving();

}
