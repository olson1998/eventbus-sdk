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
import com.olsonsolution.eventbus.domain.service.processor.Event1Processor;
import com.olsonsolution.eventbus.domain.service.publisher.StandardEventPublisher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.ImmediateKafkaEventDispatcher;
import com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion.ImmediateKafkaPublisherSubscription;
import com.olsonsolution.eventbus.domain.service.subscriber.StandardEventSubscriber;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class PublishSubscribeIntegrationTest extends EventbusIntegrationTest {

    private final Event1Processor event1Processor = new Event1Processor();

    private final Event1Processor event2Processor = new Event1Processor();

    @Test
    void shouldSendAndReceiveEvent(TestInfo testInfo) throws ExecutionException, InterruptedException, TimeoutException {
        Member publisher = StandardMember.builder()
                .name("integration-test")
                .type(MemberTypes.NODE)
                .build();
        Member subscriber = StandardMember.builder()
                .name("1")
                .type(MemberTypes.FIRST_WINS)
                .build();
        EventDestination destination = StandardEventDestination.builder()
                .product("eventbus")
                .publisher(publisher)
                .subscriber(subscriber)
                .command("test")
                .entity(TestPayload.class.getSimpleName())
                .build();
        EventDispatcher<TestPayload, ?> eventDispatcher = new ImmediateKafkaEventDispatcher<>(
                new ImmediateKafkaPublisherSubscription(destination, eventbusManager),
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
                new StandardEventPublisher<>(eventDispatcher, destination);
        EventSubscriber<TestPayload> testPayloadEventSubscriber =
                new StandardEventSubscriber<>(event1Processor, eventListener);
        testPayloadEventPublisher.register();
        testPayloadEventSubscriber.register();
        testPayloadEventSubscriber.subscribe(destination);
        EventMessage<TestPayload> testPayloadEventMessage = StandardEventMessage.<TestPayload>builder()
                .timestamp(ZonedDateTime.now())
                .content(TestPayload.fromTestInfo(testInfo))
                .build();
        CompletableFuture<Void> listeningFuture = testPayloadEventSubscriber.receive();
        CompletableFuture<?> dispatchingFuture = testPayloadEventPublisher.publish(testPayloadEventMessage);
        CompletableFuture.allOf(listeningFuture, dispatchingFuture).get(30, SECONDS);
        Collection<EventMessage<TestPayload>> receivedEvents = event1Processor.getEvents();
        assertThat(receivedEvents).hasSize(1);
        EventMessage<TestPayload> receivedEvent = CollectionUtils.extractSingleton(receivedEvents);
        assertThat(testPayloadEventMessage.getContent()).isEqualTo(receivedEvent.getContent());
    }

}
