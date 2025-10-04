package com.olsonsolution.eventbus.domain.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class KafkaTopicManager {

    private final Admin kafkaAdmin;

    public boolean createTopicIfNotPresent(String topic, int partitions, short replicationFactor) {
        try {
            kafkaAdmin.createTopics(Collections.singleton(new NewTopic(topic, partitions, replicationFactor)))
                    .all()
                    .get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return true;
    }

}
