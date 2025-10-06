package com.olsonsolution.eventbus.domain.port.repository;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import org.apache.kafka.clients.consumer.Consumer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;

import java.time.Duration;
import java.util.UUID;

public interface KafkaFactory {

    <C> KafkaSender<String, C> fabricateSender(UUID subscriptionId,
                                               AsyncAPI apiDocs,
                                               EventMapper<C> eventMapper);

    <C> KafkaReceiver<String, MappingResult<C>> fabricateReceiver(
            Duration pollInterval,
            UUID subscriptionId,
            EventDestination destination,
            AsyncAPI apiDocs,
            EventMapper<C> contentType);

    <C> Consumer<String, MappingResult<C>> fabricateConsumer(UUID subscriptionId,
                                                             EventDestination destination,
                                                             AsyncAPI apiDocs,
                                                             EventMapper<C> eventMapper);

}
