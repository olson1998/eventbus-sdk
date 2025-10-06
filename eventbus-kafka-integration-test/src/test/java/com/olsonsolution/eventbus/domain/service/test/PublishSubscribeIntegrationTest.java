package com.olsonsolution.eventbus.domain.service.test;

import com.olsonsolution.eventbus.domain.model.*;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventDispatcher;
import com.olsonsolution.eventbus.domain.port.repository.publisher.EventPublisher;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventListener;
import com.olsonsolution.eventbus.domain.port.repository.subscriber.EventSubscriber;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import com.olsonsolution.eventbus.domain.service.ContinuousKafkaEventListener;
import com.olsonsolution.eventbus.domain.service.processor.TestEventProcessor;
import com.olsonsolution.eventbus.domain.service.publisher.StandardEventPublisher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.ImmediateKafkaEventDispatcher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.ImmediateKafkaPublisherSubscription;
import com.olsonsolution.eventbus.domain.service.subscriber.StandardEventSubscriber;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class PublishSubscribeIntegrationTest extends EventbusIntegrationTest {

    private static final Member EVENT_1_PROCESSOR_MEMBER = StandardMember.builder()
            .name("event-1-processor")
            .type(MemberTypes.GROUP)
            .build();
    private static final Member EVENT_2_PROCESSOR_MEMBER = StandardMember.builder()
            .name("event-2-processor")
            .type(MemberTypes.GROUP)
            .build();
    private static final EventChannel EVENT_PROCESSOR_1_DESTINATION = StandardEventChannel.builder()
            .product("eventbus")
            .subscriber(EVENT_1_PROCESSOR_MEMBER)
            .command("test")
            .entity(TestPayload.class.getSimpleName())
            .build();
    private static final EventChannel EVENT_PROCESSOR_2_DESTINATION = StandardEventChannel.builder()
            .product("eventbus")
            .subscriber(EVENT_2_PROCESSOR_MEMBER)
            .command("test")
            .entity(TestPayload.class.getSimpleName())
            .build();

    private final TestEventProcessor event1Processor = new TestEventProcessor();

    private final TestEventProcessor event2Processor = new TestEventProcessor();

    @Test
    void shouldContinuousProcessEvents(TestInfo testInfo) throws Exception {
        EventDispatcher<TestPayload, ?> eventDispatcher = new ImmediateKafkaEventDispatcher<>(
                new ImmediateKafkaPublisherSubscription(EVENT_PROCESSOR_2_DESTINATION, eventbusManager),
                TEST_PAYLOAD_EVENT_MAPPER,
                KAFKA_FACTORY
        );
        EventListener<TestPayload, ?> eventListener = new ContinuousKafkaEventListener<>(
                Duration.ofMillis(20),
                TEST_PAYLOAD_EVENT_MAPPER,
                KAFKA_FACTORY,
                new ContinuousKafkaSubscriberSubscription(eventbusManager, Duration.ofMillis(5))
        );
        try (EventPublisher<TestPayload> testPayloadEventPublisher =
                     new StandardEventPublisher<>(eventDispatcher, EVENT_PROCESSOR_2_DESTINATION);
             EventSubscriber<TestPayload> testPayloadEventSubscriber =
                     new StandardEventSubscriber<>(event2Processor, eventListener)) {
            testPayloadEventPublisher.register();
            testPayloadEventSubscriber.register();
            testPayloadEventSubscriber.subscribe(EVENT_PROCESSOR_2_DESTINATION);
            testPayloadEventSubscriber.receive();
            for (int i = 0; i < 5; i++) {
                sleep(400);
                dispatchTestEvents(testPayloadEventPublisher, 10, testInfo).get(30, SECONDS);
            }
            awaitUntilAllReceived(event2Processor, 50, Duration.ofSeconds(15));
        }
        Collection<EventMessage<TestPayload>> receivedEvents = event2Processor.getEvents();
        assertThat(receivedEvents).hasSize(50);
    }

    private CompletableFuture<Void> dispatchTestEvents(EventPublisher<TestPayload> eventPublisher,
                                                       int qty,
                                                       TestInfo testInfo) {
        return IntStream.range(0, qty)
                .mapToObj(i -> StandardEventMessage.<TestPayload>eventMessageBuilder()
                        .headers(Map.ofEntries(
                                new DefaultMapEntry<>("x-accept", 1),
                                new DefaultMapEntry<>("x-sequence", i)
                        ))
                        .timestamp(ZonedDateTime.now())
                        .content(TestPayload.fromTestInfo(testInfo))
                        .build())
                .collect(Collectors.collectingAndThen(
                        Collectors.toUnmodifiableList(),
                        events -> events.stream().map(eventPublisher::publish)
                )).collect(Collectors.collectingAndThen(
                        Collectors.toUnmodifiableList(),
                        dispatchFutures ->
                                CompletableFuture.allOf(dispatchFutures.toArray(CompletableFuture[]::new))
                ));
    }

    private void awaitUntilAllReceived(TestEventProcessor processor, int messagesQty, Duration maxAwait) {
        int received = 0;
        Instant timestamp = Instant.now();
        Instant deadline = timestamp.plus(maxAwait);
        while (received != messagesQty) {
            received = processor.getEvents().size();
            if (received == messagesQty) {
                break;
            }
            if (Instant.now().isAfter(deadline)) {
                throw new IllegalStateException(
                        "Timeout waiting for all events to be received, received: %d, expected: %d"
                                .formatted(received, messagesQty)
                );
            }
        }
    }

}
