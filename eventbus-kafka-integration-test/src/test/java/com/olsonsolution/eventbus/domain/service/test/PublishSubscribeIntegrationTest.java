package com.olsonsolution.eventbus.domain.service.test;

import com.olsonsolution.eventbus.domain.model.*;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventPublisher;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import com.olsonsolution.eventbus.domain.service.OnDemandKafkaEventListener;
import com.olsonsolution.eventbus.domain.service.processor.TestEventProcessor;
import com.olsonsolution.eventbus.domain.service.publisher.StandardEventPublisher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.ImmediateKafkaEventDispatcher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.ImmediateKafkaPublisherSubscription;
import com.olsonsolution.eventbus.domain.service.subscriber.StandardEventSubscriber;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class PublishSubscribeIntegrationTest extends EventbusIntegrationTest {

    private static final Member EVENT_1_PROCESSOR_MEMBER = StandardMember.builder()
            .name("event-1-processor")
            .type(MemberTypes.GROUP)
            .build();
    private static final EventDestination EVENT_PROCESSOR_1_DESTINATION = StandardEventDestination.builder()
            .product("eventbus")
            .subscriber(EVENT_1_PROCESSOR_MEMBER)
            .command("test")
            .entity(TestPayload.class.getSimpleName())
            .build();

    private final TestEventProcessor event1Processor = new TestEventProcessor();

    @Test
    void shouldSendAndReceiveEvent(TestInfo testInfo) throws Exception {
        EventDispatcher<TestPayload, ?> eventDispatcher = new ImmediateKafkaEventDispatcher<>(
                new ImmediateKafkaPublisherSubscription(EVENT_PROCESSOR_1_DESTINATION, eventbusManager),
                TEST_PAYLOAD_EVENT_MAPPER,
                KAFKA_FACTORY,
                OBJECT_MAPPER
        );
        EventListener<TestPayload, ?> eventListener = new OnDemandKafkaEventListener<>(
                new OnDemandKafkaSubscriberSubscription(eventbusManager),
                TEST_PAYLOAD_EVENT_MAPPER,
                KAFKA_FACTORY
        );
        EventPublisher<TestPayload> testPayloadEventPublisher =
                new StandardEventPublisher<>(eventDispatcher, EVENT_PROCESSOR_1_DESTINATION);
        EventSubscriber<TestPayload> testPayloadEventSubscriber =
                new StandardEventSubscriber<>(event1Processor, eventListener);
        testPayloadEventPublisher.register();
        testPayloadEventSubscriber.register();
        testPayloadEventSubscriber.subscribe(EVENT_PROCESSOR_1_DESTINATION);
        EventMessage<TestPayload> testPayloadEventMessage = StandardEventMessage.<TestPayload>eventMessageBuilder()
                .headers(Map.ofEntries(
                        new DefaultMapEntry<>("accept", 1),
                        new DefaultMapEntry<>("testInfo", testInfo.getDisplayName())
                ))
                .timestamp(ZonedDateTime.now())
                .content(TestPayload.fromTestInfo(testInfo))
                .build();
        CompletableFuture<Void> listeningFuture = testPayloadEventSubscriber.receive();
        Mono.just(testPayloadEventMessage)
                .delayElement(Duration.ofSeconds(3))
                .flatMap(event ->
                        Mono.fromFuture(() -> testPayloadEventPublisher.publish(event)))
                .block();
        listeningFuture.get(30, SECONDS);
        Collection<EventMessage<TestPayload>> receivedEvents = event1Processor.getEvents();
        assertThat(receivedEvents).hasSize(1);
        EventMessage<TestPayload> receivedEvent = CollectionUtils.extractSingleton(receivedEvents);
        assertThat(testPayloadEventMessage.getContent()).isEqualTo(receivedEvent.getContent());
        testPayloadEventPublisher.close();
        testPayloadEventSubscriber.close();
    }

}
