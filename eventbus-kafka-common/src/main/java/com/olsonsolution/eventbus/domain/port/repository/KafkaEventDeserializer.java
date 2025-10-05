package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventMessage;
import org.apache.kafka.common.serialization.Deserializer;

public interface KafkaEventDeserializer<T> extends Deserializer<EventMessage<T>> {

}
