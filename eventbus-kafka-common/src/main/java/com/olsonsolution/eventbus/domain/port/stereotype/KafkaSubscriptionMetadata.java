package com.olsonsolution.eventbus.domain.port.stereotype;

public interface KafkaSubscriptionMetadata extends SubscriptionMetadata {

    String getTopic();

    Integer getPartition();

}
