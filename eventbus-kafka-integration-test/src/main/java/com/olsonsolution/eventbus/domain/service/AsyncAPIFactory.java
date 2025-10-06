package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.schemas.asyncapi.AsyncAPISchema;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.info.Info;
import com.asyncapi.v3._0_0.model.server.Server;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.apache.kafka.common.security.auth.SecurityProtocol.PLAINTEXT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AsyncAPIFactory {

    static AsyncAPI fabricateAsyncAPI(@NonNull String bootstrapServers,
                                      Collection<EventChannel> destinations) {
        return AsyncAPI.builder()
                .servers(fabricateServers(bootstrapServers))
                .info(fabricateInfo())
                .channels(fabricateChannels(destinations))
                .defaultContentType("application/json")
                .build();
    }

    private static Map<String, Object> fabricateServers(String bootstrapServers) {
        Server server = Server.builder()
                .title("test container kafka")
                .description("Test container for kafka")
                .host(bootstrapServers)
                .protocol(PLAINTEXT.name())
                .build();
        return Collections.singletonMap("kafka", server);
    }

    private static Info fabricateInfo() {
        return Info.builder()
                .title("eventbus-kafka-integration-test")
                .version("test")
                .description("Integration test for eventbus SDK")
                .build();
    }

    private static Map<String, Object> fabricateChannels(Collection<EventChannel> destinations) {
        Stream.Builder<Map.Entry<String, Object>> channels = Stream.builder();
        destinations.forEach(destination -> fabricateChannel(destination, channels));
        return channels.build().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void fabricateChannel(EventChannel destination,
                                         Stream.Builder<Map.Entry<String, Object>> channels) {
        String channelName = destination.toString();
        String description = "Subscription: subscriber: %s operation %s.%s".formatted(
                destination.getSubscriber(),
                destination.getCommand(),
                destination.getEntity()
        );
        Channel channel = Channel.builder()
                .title(channelName)
                .description(description)
                .messages(Collections.singletonMap("TestPayload", new AsyncAPISchema()))
                .build();
        channel.setBindings(fabricateBindings(destination));
        channels.add(entry(channelName, channel));
    }

    private static Map<String, Object> fabricateBindings(EventChannel destination) {
        String topic = destination.toString();
        KafkaChannelBinding kafkaChannelBinding = KafkaChannelBinding.builder()
                .topic(topic)
                .partitions(1)
                .replicas(1)
                .build();
        return Collections.singletonMap("kafka", kafkaChannelBinding);
    }

}
