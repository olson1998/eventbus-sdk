package com.olsonsolution.eventbus.domain.port.stereotype.kafka;

import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;

public interface CorruptedKafkaEventMessage<C> extends KafkaEventMessage<C>, CorruptedEventMessage<C> {
}
