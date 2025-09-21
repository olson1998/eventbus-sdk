package com.olsonsolution.eventbus.domain.service.publisher.kafka.subscripion;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KafkaPublisherSubscription implements PublisherSubscription<KafkaSubscriptionMetadata> {

    @Getter
    private final KafkaSubscriptionMetadata metadata;

    @Override
    public void renew() {

    }
}
