package com.olsonsolution.eventbus.domain.port.stereotype.kafka;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;

public interface KafkaEventMessage<C> extends EventMessage<C> {

    String getKey();

    String getTopic();

    int getPartition();

    long getOffset();

}
