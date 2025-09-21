package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;

public interface EventbusManager<M extends SubscriptionMetadata> {

    M requestMetadata(EventDestination destination);

}
