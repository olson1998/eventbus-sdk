package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import lombok.Getter;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

@Getter
public class KafkaSendRecordDispatchException extends EventDispatchException {

    private static final String MESSAGE = "Event for destination=%s was not sent to kafka correctly";

    private final SenderRecord<String, ?, UUID> senderRecord;

    private final EventDestination destination;

    private final KafkaSubscriptionMetadata kafkaSubscriptionMetadata;

    public KafkaSendRecordDispatchException(Throwable cause,
                                            SenderRecord<String, ?, UUID> senderRecord,
                                            EventDestination destination,
                                            KafkaSubscriptionMetadata kafkaSubscriptionMetadata) {
        super(MESSAGE.formatted(destination), cause);
        this.senderRecord = senderRecord;
        this.destination = destination;
        this.kafkaSubscriptionMetadata = kafkaSubscriptionMetadata;
    }
}
