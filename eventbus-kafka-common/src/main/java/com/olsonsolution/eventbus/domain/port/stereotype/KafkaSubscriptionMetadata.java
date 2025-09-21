package com.olsonsolution.eventbus.domain.port.stereotype;

import lombok.NonNull;

import java.util.Collection;

public interface KafkaSubscriptionMetadata extends SubscriptionMetadata {

    @NonNull
    String getTopic();

    @NonNull
    Collection<Integer> getPartition();

}
