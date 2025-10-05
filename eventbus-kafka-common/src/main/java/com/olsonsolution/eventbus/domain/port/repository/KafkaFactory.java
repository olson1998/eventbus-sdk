package com.olsonsolution.eventbus.domain.port.repository;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.sender.KafkaSender;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface KafkaFactory {

    <C> KafkaSender<String, C> fabricateSender(UUID subscriptionId,
                                               AsyncAPI apiDocs,
                                               EventMapper<C> eventMapper);

    <C> KafkaReceiver<String, EventMessage<C>> fabricateReceiver(
           UUID subscriptionId,
            EventDestination destination,
            AsyncAPI apiDocs,
            EventMapper<C> contentType,
            Consumer<Collection<ReceiverPartition>> assignmentListener);

}
