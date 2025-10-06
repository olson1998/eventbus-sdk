package com.olsonsolution.eventbus.domain.service;

import com.olsonsolution.eventbus.domain.port.repository.EventMapper;
import com.olsonsolution.eventbus.domain.port.repository.KafkaFactory;
import com.olsonsolution.eventbus.domain.port.repository.processor.EventProcessor;
import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.service.subscription.OnDemandKafkaSubscriberSubscription;

import java.time.Duration;

public class OnDemandKafkaEventListener<C> extends KafkaEventListener<C, OnDemandKafkaSubscriberSubscription> {

    public OnDemandKafkaEventListener(Duration maxPollInterval,
                                      EventMapper<C> eventMapper,
                                      KafkaFactory kafkaFactory,
                                      OnDemandKafkaSubscriberSubscription subscription) {
        super(maxPollInterval, eventMapper, kafkaFactory, subscription);
    }

    @Override
    public void listen(EventProcessor<C> eventProcessor) {

    }

    @Override
    public void stopListening() {

    }

    @Override
    public void subscribe(EventChannel destination) {

    }

    @Override
    public void unsubscribe(EventChannel destination) {

    }

    @Override
    public void close() throws Exception {

    }
}
