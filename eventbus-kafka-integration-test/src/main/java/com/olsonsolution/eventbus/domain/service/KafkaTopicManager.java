package com.olsonsolution.eventbus.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class KafkaTopicManager {

    private final Admin kafkaAdmin;

    @SneakyThrows
    public void createTopicIfNotPresent(String topic, int partitions, short replicationFactor) {
        DescribeTopicsResult result = kafkaAdmin.describeTopics(Collections.singleton(topic));
        Map<String, TopicDescription> topics = result.allTopicNames().get();
        if (topics.isEmpty()) {
            kafkaAdmin.createTopics(Collections.singleton(new NewTopic(topic, partitions, replicationFactor)))
                    .all()
                    .get();
        }
    }

}
