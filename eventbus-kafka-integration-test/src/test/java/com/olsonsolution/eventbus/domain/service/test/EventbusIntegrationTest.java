package com.olsonsolution.eventbus.domain.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olsonsolution.eventbus.domain.model.TestPayload;
import com.olsonsolution.eventbus.domain.port.props.KafkaClusterProperties;
import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.service.ClassEventMapper;
import com.olsonsolution.eventbus.domain.service.KafkaTopicManager;
import com.olsonsolution.eventbus.domain.service.StandardKafkaFactory;
import com.olsonsolution.eventbus.domain.service.TestEventbusManager;
import org.apache.kafka.clients.admin.Admin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.admin.AdminClientConfig.CLIENT_ID_CONFIG;

abstract class EventbusIntegrationTest {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected static final KafkaFactory KAFKA_FACTORY = new StandardKafkaFactory(new KafkaClusterProperties() {
        @Override
        public Properties getProducer() {
            return new Properties();
        }

        @Override
        public Properties getConsumer() {
            return new Properties();
        }
    });

    protected static final EventMapper<TestPayload> TEST_PAYLOAD_EVENT_MAPPER =
            new ClassEventMapper<>(TestPayload.class.getSimpleName(), OBJECT_MAPPER, TestPayload.class);

    private static final ConfluentKafkaContainer KAFKA_CONTAINER =
            new ConfluentKafkaContainer("confluentinc/cp-kafka:7.7.5")
                    .withCreateContainerCmdModifier(cmd -> cmd.withName("kafka-" + UUID.randomUUID()))
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                    .withEnv("KAFKA_NUM_PARTITIONS", "1")
                    .withEnv("KAFKA_DEFAULT_REPLICATION_FACTOR", "1");


    private static Admin kafkaAdmin;

    private static KafkaTopicManager kafkaTopicManager;

    protected static TestEventbusManager eventbusManager;

    @BeforeAll
    static void setupEventbus() {
        KAFKA_CONTAINER.start();
        kafkaAdmin = createKafkaAdmin();
        kafkaTopicManager = new KafkaTopicManager(kafkaAdmin);
        eventbusManager = new TestEventbusManager(
                Duration.ofMinutes(1),
                KAFKA_CONTAINER.getBootstrapServers(),
                kafkaTopicManager
        );
    }

    @AfterAll
    static void closeKafka() {
        kafkaAdmin.close();
        KAFKA_CONTAINER.close();
        kafkaAdmin = null;
        kafkaTopicManager = null;
        eventbusManager = null;
    }

    private static Admin createKafkaAdmin() {
        Properties properties = new Properties();
        properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        properties.setProperty(CLIENT_ID_CONFIG, EventbusIntegrationTest.class.getCanonicalName());
        return Admin.create(properties);
    }

}
