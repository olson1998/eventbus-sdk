package com.olsonsolution.eventbus.domain.model.kafka;

import com.olsonsolution.eventbus.domain.model.StandardSubscriptionMetadata;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StandardKafkaSubscriptionMetadata extends StandardSubscriptionMetadata
        implements KafkaSubscriptionMetadata {

    private final String topic;

    private final Integer partition;

    @Builder(builderMethodName = "kafkaBuilder")
    public StandardKafkaSubscriptionMetadata(UUID id,
                                             ZonedDateTime expireAt,
                                             ZonedDateTime createdAt,
                                             String topic,
                                             Integer partition) {
        super(id, expireAt, createdAt);
        this.topic = topic;
        this.partition = partition;
    }

}
