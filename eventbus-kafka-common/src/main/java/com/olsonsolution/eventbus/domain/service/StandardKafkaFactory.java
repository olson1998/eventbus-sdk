package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.server.Server;
import com.olsonsolution.eventbus.domain.model.MemberTypes;
import com.olsonsolution.eventbus.domain.port.props.KafkaClusterProperties;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import com.olsonsolution.eventbus.domain.port.stereotype.MemberType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.CommonClientConfigs.*;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;

@Slf4j
@RequiredArgsConstructor
public class StandardKafkaFactory implements KafkaFactory {

    private final KafkaClusterProperties kafkaClusterProperties;

    private final StringSerializer stringSerializer = new StringSerializer();

    private final StringDeserializer stringDeserializer = new StringDeserializer();

    @Override
    public <C> KafkaSender<String, C> fabricateSender(UUID subscriptionId,
                                                      AsyncAPI apiDocs,
                                                      EventMapper<C> eventMapper) {
        StandardKafkaEventSerializer<C> kafkaEventSerializer = new StandardKafkaEventSerializer<>(apiDocs, eventMapper);
        Properties producerProperties = new Properties(kafkaClusterProperties.getProducer());
        producerProperties.put(BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers(apiDocs.getServers()));
        producerProperties.put(CLIENT_ID_CONFIG, getPublisherId(subscriptionId));
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, C> senderOptions = SenderOptions.<String, C>create(producerProperties)
                .withKeySerializer(stringSerializer)
                .withValueSerializer(kafkaEventSerializer);
        return KafkaSender.create(senderOptions);
    }

    @Override
    public <C> KafkaReceiver<String, MappingResult<C>> fabricateReceiver(Duration pollInterval,
                                                                         UUID subscriptionId,
                                                                         String groupId,
                                                                         AsyncAPI apiDocs,
                                                                         EventMapper<C> eventMapper) {
        Deserializer<MappingResult<C>> kafkaEventDeserializer =
                new StandardKafkaEventDeserializer<>(apiDocs, eventMapper);
        Properties consumerProperties = getConsumerProperties(subscriptionId, groupId, apiDocs);
        ReceiverOptions<String, MappingResult<C>> receiverOptions =
                ReceiverOptions.<String, MappingResult<C>>create(consumerProperties)
                        .withKeyDeserializer(stringDeserializer)
                        .withValueDeserializer(kafkaEventDeserializer)
                        .subscription(KafkaAsyncAPIUtils.collectTopics(apiDocs))
                        .commitInterval(Duration.ZERO)
                        .pollTimeout(pollInterval);
        return KafkaReceiver.create(receiverOptions);
    }

    @Override
    public <C> Consumer<String, MappingResult<C>> fabricateConsumer(UUID subscriptionId,
                                                                    String groupId,
                                                                    AsyncAPI apiDocs,
                                                                    EventMapper<C> eventMapper) {
        Deserializer<MappingResult<C>> kafkaEventDeserializer =
                new StandardKafkaEventDeserializer<>(apiDocs, eventMapper);
        Properties consumerProperties = getConsumerProperties(subscriptionId, groupId, apiDocs);
        return new KafkaConsumer<>(consumerProperties, stringDeserializer, kafkaEventDeserializer);
    }

    private Properties getConsumerProperties(UUID subscriptionId, String groupId, AsyncAPI apiDocs) {
        Properties consumerProperties = new Properties(kafkaClusterProperties.getConsumer());
        String clientId = getSubscriberId(subscriptionId);
        consumerProperties.put(BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers(apiDocs.getServers()));
        consumerProperties.put(CLIENT_ID_CONFIG, clientId);
        consumerProperties.put(GROUP_ID_CONFIG, groupId);
        consumerProperties.put(GROUP_INSTANCE_ID_CONFIG, clientId);
        consumerProperties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProperties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerProperties;
    }

    private String getPublisherId(UUID subscriptionId) {
        return "publisher-" + subscriptionId.toString();
    }

    private String getSubscriberId(UUID subscriptionId) {
        return "subscriber-" + subscriptionId.toString();
    }

    private String getGroupId(EventChannel destination, UUID subscriptionId) {
        Member subscriber = destination.getSubscriber();
        MemberType subscriberType = subscriber.getType();
        StringBuilder groupId = new StringBuilder("subscriber-")
                .append(subscriberType.getIdentifier());
        if (subscriberType.isEqualTo(MemberTypes.FIRST_WINS) ||
                subscriberType.isEqualTo(MemberTypes.GROUP)) {
            groupId.append(subscriber.getName());
        } else {
            groupId.append(subscriptionId.toString());
        }
        return groupId.toString();
    }

    private String getBootstrapServers(Map<String, Object> servers) {
        return MapUtils.emptyIfNull(servers)
                .values()
                .stream()
                .filter(Server.class::isInstance)
                .map(Server.class::cast)
                .map(Server::getHost)
                .collect(Collectors.joining(", "));
    }

}
