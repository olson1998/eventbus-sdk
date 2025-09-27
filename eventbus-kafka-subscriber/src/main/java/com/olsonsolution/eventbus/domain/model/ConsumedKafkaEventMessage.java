package com.olsonsolution.eventbus.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConsumedKafkaEventMessage<C> extends StandardEventMessage<C> {

    private final String key;

    private final String topic;

    private final int partition;

    private final long offset;

    @Builder(builderMethodName = "consumedKafkaEventMessageBuilder")
    public ConsumedKafkaEventMessage(C content,
                                     Map<String, Object> headers,
                                     ZonedDateTime timestamp,
                                     String key,
                                     String topic,
                                     int partition,
                                     long offset) {
        super(content, headers, timestamp);
        this.key = key;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }
}
