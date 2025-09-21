package com.olsonsolution.eventbus.domain.model.kafka;

import com.olsonsolution.eventbus.domain.model.StandardEventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TopicPartitionDestination extends StandardEventDestination {

    private final Integer partition;

    @Builder(builderClassName = "KafkaTopicEventDestinationBuilder", builderMethodName = "kafkaTopicDestination")
    public TopicPartitionDestination(@NonNull String product,
                                     @NonNull Member publisher,
                                     @NonNull Member subscriber,
                                     @NonNull String entity,
                                     @NonNull String command,
                                     Integer partition) {
        super(product, publisher, subscriber, entity, command);
        this.partition = partition;
    }

    public String getTopic() {
        return super.toString();
    }

    @Override
    public String toString() {
        StringBuilder topicPartitionBuilder = new StringBuilder(getTopic());
        if (partition != null) {
            topicPartitionBuilder.append("-").append(partition);
        }
        return topicPartitionBuilder.toString();
    }
}
