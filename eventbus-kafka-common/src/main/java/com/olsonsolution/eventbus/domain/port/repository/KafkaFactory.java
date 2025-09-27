package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;

import java.util.UUID;

public interface KafkaFactory {

    <C> KafkaSender<String, C> fabricateSender(UUID subscriptionId);

    <C> KafkaReceiver<String, C> fabricateReceiver(UUID subscriptionId,
                                                                 EventDestination destination,
                                                                 Class<C> contentType);

}
