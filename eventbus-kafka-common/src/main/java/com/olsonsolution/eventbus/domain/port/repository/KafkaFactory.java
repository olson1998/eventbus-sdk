package com.olsonsolution.eventbus.domain.port.repository;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface KafkaFactory {

    <C> SenderOptions<String, C> fabricateSender(UUID subscriptionId,
                                                 AsyncAPI apiDocs,
                                                 EventMapper<C> eventMapper);

    <C> ReceiverOptions<String, MappingResult<C>> fabricateReceiver(
            Duration pollInterval,
            UUID subscriptionId,
            EventDestination destination,
            AsyncAPI apiDocs,
            EventMapper<C> contentType);

}
