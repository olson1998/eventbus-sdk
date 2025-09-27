package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.model.MemberTypes;
import com.olsonsolution.eventbus.domain.port.props.KafkaClusterProperties;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import com.olsonsolution.eventbus.domain.port.stereotype.MemberType;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.CommonClientConfigs.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;

@RequiredArgsConstructor
public class StandardKafkaFactory implements KafkaFactory {

    private final EventMapper eventMapper;

    private final KafkaClusterProperties kafkaClusterProperties;

    private final StringSerializer stringSerializer = new StringSerializer();

    private final StringDeserializer stringDeserializer = new StringDeserializer();

    @Override
    public <C> KafkaSender<String, C> fabricateSender(UUID subscriptionId) {
        StandardKafkaEventSerializer<C> kafkaEventSerializer = new StandardKafkaEventSerializer<>(eventMapper);
        Properties producerProperties = new Properties(kafkaClusterProperties.getProducer());
        producerProperties.put(CLIENT_ID_CONFIG, getClientId(subscriptionId));
        producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, C> senderOptions = SenderOptions.<String, C>create(producerProperties)
                .withKeySerializer(stringSerializer)
                .withValueSerializer(kafkaEventSerializer);
        return KafkaSender.create(senderOptions);
    }

    @Override
    public <C> KafkaReceiver<String, C> fabricateReceiver(UUID subscriptionId,
                                                          EventDestination destination,
                                                          Class<C> contentType) {
        StandardKafkaEventDeserializer<C> kafkaEventDeserializer =
                new StandardKafkaEventDeserializer<>(contentType, eventMapper);
        Properties consumerProperties = new Properties(kafkaClusterProperties.getConsumer());
        String clientId = getClientId(subscriptionId);
        String groupId = getGroupId(destination, subscriptionId);
        consumerProperties.put(CLIENT_ID_CONFIG, clientId);
        consumerProperties.put(GROUP_ID_CONFIG, groupId);
        consumerProperties.put(GROUP_INSTANCE_ID_CONFIG, clientId);
        consumerProperties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ReceiverOptions<String, C> receiverOptions = ReceiverOptions.<String, C>create(consumerProperties)
                .withKeyDeserializer(stringDeserializer)
                .withValueDeserializer(kafkaEventDeserializer);
        return KafkaReceiver.create(receiverOptions);
    }

    private String getClientId(UUID subscriptionId) {
        return "subscription-" + subscriptionId.toString();
    }

    private String getGroupId(EventDestination destination, UUID subscriptionId) {
        Member subscriber = destination.getSubscriber();
        MemberType subscriberType = subscriber.getType();
        StringBuilder groupId = new StringBuilder("subscriber-")
                .append(subscriberType.getIdentifier());
        if (subscriberType.isEqualTo(MemberTypes.FIRST_WINS) ||
                subscriberType.isEqualTo(MemberTypes.NODE_GROUP)) {
            groupId.append(subscriber.getName());
        } else {
            groupId.append(subscriptionId.toString());
        }
        return groupId.toString();
    }

}
