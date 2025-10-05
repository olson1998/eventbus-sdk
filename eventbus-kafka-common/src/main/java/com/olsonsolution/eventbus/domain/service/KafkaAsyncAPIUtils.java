package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.olsonsolution.eventbus.domain.model.kafka.AsyncAPIExtensions.X_PARTITION_EXTENSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaAsyncAPIUtils {

    public static Collection<TopicPartition> collectTopicPartitions(AsyncAPI apiDocs) {
        return MapUtils.emptyIfNull(apiDocs.getChannels())
                .values()
                .stream()
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast)
                .flatMap(KafkaAsyncAPIUtils::getTopicPartitionForChannel)
                .toList();
    }

    private static Stream<TopicPartition> getTopicPartitionForChannel(Channel channel) {
        return Optional.ofNullable(channel.getBindings())
                .flatMap(binding -> Optional.ofNullable(binding.get("kafka")))
                .filter(KafkaChannelBinding.class::isInstance)
                .map(KafkaChannelBinding.class::cast)
                .map(KafkaAsyncAPIUtils::getTopicPartitionForKafkaChannel)
                .stream();
    }

    private static TopicPartition getTopicPartitionForKafkaChannel(KafkaChannelBinding kafkaChannelBinding) {
        String topic = kafkaChannelBinding.getTopic();
        return getTopicPartitionForKafkaChannel(topic, kafkaChannelBinding.getExtensionFields());
    }

    private static TopicPartition getTopicPartitionForKafkaChannel(String topic,
                                                                   Map<String, Object> extensionFields) {
        int partition = 0;
        if (extensionFields != null &&
                extensionFields.containsKey(X_PARTITION_EXTENSION) &&
                extensionFields.get(X_PARTITION_EXTENSION) instanceof Integer channelPartition) {
            partition = channelPartition;
        }
        return new TopicPartition(topic, partition);
    }

}
