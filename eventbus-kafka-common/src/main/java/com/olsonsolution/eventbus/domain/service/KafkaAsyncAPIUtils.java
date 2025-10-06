package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaAsyncAPIUtils {

    public static Collection<String> collectTopics(AsyncAPI apiDocs) {
        return MapUtils.emptyIfNull(apiDocs.getChannels())
                .values()
                .stream()
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast)
                .flatMap(KafkaAsyncAPIUtils::getTopicsForChannel)
                .toList();
    }

    private static Stream<String> getTopicsForChannel(Channel channel) {
        return Optional.ofNullable(channel.getBindings())
                .flatMap(binding -> Optional.ofNullable(binding.get("kafka")))
                .filter(KafkaChannelBinding.class::isInstance)
                .map(KafkaChannelBinding.class::cast)
                .map(KafkaChannelBinding::getTopic)
                .stream();
    }

}
