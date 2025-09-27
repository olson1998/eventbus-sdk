package com.olsonsolution.eventbus.domain.service.publisher.kafka;

import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.ImmediateKafkaPublisherSubscription;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

import java.util.List;

public class ImmediateKafkaEventDispatcher<C> extends KafkaEventDispatcher<C, ImmediateKafkaPublisherSubscription> {

    public ImmediateKafkaEventDispatcher(ImmediateKafkaPublisherSubscription subscription, KafkaFactory kafkaFactory) {
        super(subscription, kafkaFactory);
    }

    @Override
    public Mono<List<EventAcknowledgment>> dispatch(EventMessage<C> message) {
        return prepareSenderRecords(message)
                .flatMap(this::dispatchSenderRecords);
    }

}
