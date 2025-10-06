package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import com.olsonsolution.eventbus.domain.service.subscription.ContinuousKafkaSubscriberSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;

import java.time.Duration;

@Slf4j
public class ContinuousKafkaEventListener<C> extends KafkaEventListener<C, ContinuousKafkaSubscriberSubscription> {

    public ContinuousKafkaEventListener(Duration maxPollInterval,
                                        ContinuousKafkaSubscriberSubscription subscription,
                                        EventMapper<C> eventMapper,
                                        KafkaFactory kafkaFactory) {
        super(maxPollInterval, subscription, log, eventMapper, kafkaFactory);
    }

    @Override
    protected Disposable subscribeToReceiver(KafkaReceiver<String, MappingResult<C>> kafkaReceiver,
                                             EventProcessor<C> eventProcessor) {
        return kafkaReceiver.receive()
                .subscribe(record -> processEvent(record, eventProcessor));
    }

}
