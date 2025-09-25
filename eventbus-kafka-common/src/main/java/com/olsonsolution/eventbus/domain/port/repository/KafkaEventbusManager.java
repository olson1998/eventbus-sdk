package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.KafkaSubscriptionMetadata;

public interface KafkaEventbusManager extends EventbusManager<KafkaSubscriptionMetadata> {

    KafkaSubscriptionMetadata getSubscriptionMetadataByDestination(EventDestination destination);

}
