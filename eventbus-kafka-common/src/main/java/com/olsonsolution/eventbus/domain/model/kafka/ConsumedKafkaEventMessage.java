package com.olsonsolution.eventbus.domain.model.kafka;

import com.olsonsolution.eventbus.domain.model.StandardEventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.kafka.KafkaEventMessage;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ConsumedKafkaEventMessage<C> extends StandardEventMessage<C> implements KafkaEventMessage<C> {

    private final String key;

    private final int partition;

    private final long offset;

    private final String topic;

    @Builder(builderMethodName = "kafkaEventMessageBuilder")
    public ConsumedKafkaEventMessage(C content, Map<String, Object> headers, ZonedDateTime timestamp,
                                     String key, int partition, long offset, String topic) {
        super(content, headers, timestamp);
        this.key = key;
        this.partition = partition;
        this.offset = offset;
        this.topic = topic;
    }


}
