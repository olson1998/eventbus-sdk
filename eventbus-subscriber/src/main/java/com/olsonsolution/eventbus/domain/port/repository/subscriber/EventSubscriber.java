package com.olsonsolution.eventbus.domain.port.repository.subscriber;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface EventSubscriber<P extends EventProcessor> extends Participant {

    void subscribe(EventDestination destination);

    void receive();

}
