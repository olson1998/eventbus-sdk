package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface EventSubscriber extends Participant {

    void subscribe(EventDestination destination);

    void receive();

}
