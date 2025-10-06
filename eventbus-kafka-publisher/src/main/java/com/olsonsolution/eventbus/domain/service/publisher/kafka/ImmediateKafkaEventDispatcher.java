package com.olsonsolution.eventbus.domain.service.publisher.kafka;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.ImmediateKafkaPublisherSubscription;
import reactor.core.publisher.Mono;

import java.util.List;

public class ImmediateKafkaEventDispatcher<C> extends KafkaEventDispatcher<C, ImmediateKafkaPublisherSubscription> {

    public ImmediateKafkaEventDispatcher(ImmediateKafkaPublisherSubscription subscription,
                                         EventMapper<C> eventMapper,
                                         KafkaFactory kafkaFactory) {
        super(subscription, eventMapper, kafkaFactory);
    }

    @Override
    public Mono<List<EventAcknowledgment>> dispatch(EventMessage<C> message) {
        return prepareSenderRecords(message)
                .flatMap(this::dispatchSenderRecords);
    }

}
