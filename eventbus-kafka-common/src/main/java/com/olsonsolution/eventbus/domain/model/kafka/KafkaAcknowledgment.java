package com.olsonsolution.eventbus.domain.model.kafka;

import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import lombok.Data;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

@Data
public class KafkaAcknowledgment implements EventAcknowledgment {

    private final RecordMetadata recordMetadata;

    @Override
    public ZonedDateTime getTimestamp() {
        return Instant.ofEpochSecond(recordMetadata.timestamp())
                .atZone(UTC);
    }
}
