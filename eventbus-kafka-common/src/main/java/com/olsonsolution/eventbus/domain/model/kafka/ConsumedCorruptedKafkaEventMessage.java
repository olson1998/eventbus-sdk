package com.olsonsolution.eventbus.domain.model.kafka;

import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventMappingException;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.CorruptedKafkaEventMessage;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConsumedCorruptedKafkaEventMessage<C> extends ConsumedKafkaEventMessage<C>
        implements CorruptedKafkaEventMessage<C> {

    private final EventMappingException corruptionCause;

    @Builder(builderMethodName = "kafkaCorruptedEventMessageBuilder")
    public ConsumedCorruptedKafkaEventMessage(Map<String, Object> headers,
                                              ZonedDateTime timestamp,
                                              String key,
                                              int partition,
                                              long offset,
                                              String topic,
                                              EventMappingException corruptionCause) {
        super(null, headers, timestamp, key, partition, offset, topic);
        this.corruptionCause = corruptionCause;
    }
}
