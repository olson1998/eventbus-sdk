package com.olsonsolution.eventbus.domain.service.publisher.kafka;

import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.KafkaPublisherSubscription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class KafkaEventDispatcher<C, S extends KafkaPublisherSubscription>
        implements EventDispatcher<C, S, KafkaSubscriptionMetadata> {

    @Getter
    private S subscription;

    private final KafkaSender<String, C> kafkaSender;

    @Override
    public void subscribe() {

    }

    protected Flux<SenderResult<UUID>> dispatchEvent(EventMessage<C> message, UUID correlationId) {
        return kafkaSender.send(Mono.fromSupplier(() -> mapToSenderRecord(message, correlationId)));
    }

    private SenderRecord<String, C, UUID> mapToSenderRecord(EventMessage<C> message, UUID correlationId) {
        KafkaSubscriptionMetadata metadata = subscription.getMetadata();
        return SenderRecord.create(
                metadata.getTopic(),
                metadata.getPartition(),
                message.getTimestamp().toInstant().toEpochMilli(),
                null,
                message.getContent(),
                correlationId
        );
    }

}
