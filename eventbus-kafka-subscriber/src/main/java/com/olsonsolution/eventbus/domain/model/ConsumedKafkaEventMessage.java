package com.olsonsolution.eventbus.domain.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConsumedKafkaEventMessage<C> extends StandardEventMessage<C> {

    private final String topic;

    private final long offset;

    private final int partition;

    @Builder(builderMethodName = "consumedKafkaEventBuilder")
    public ConsumedKafkaEventMessage(C content,
                                     @Singular("header") Map<String, Object> headers,
                                     ZonedDateTime timestamp,
                                     String topic,
                                     long offset,
                                     int partition) {
        super(content, headers, timestamp);
        this.topic = topic;
        this.offset = offset;
        this.partition = partition;
    }
}
