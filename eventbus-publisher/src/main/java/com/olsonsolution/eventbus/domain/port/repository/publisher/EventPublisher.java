package com.olsonsolution.eventbus.domain.port.repository.publisher;

import com.olsonsolution.eventbus.domain.port.repository.Participant;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventPublisher<C> extends Participant {

    EventChannel getDestination();

    CompletableFuture<List<EventAcknowledgment>> publish(EventMessage<C> message);

}
