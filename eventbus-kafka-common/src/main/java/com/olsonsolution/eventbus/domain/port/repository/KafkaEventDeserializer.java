package com.olsonsolution.eventbus.domain.port.repository;

import org.apache.kafka.common.serialization.Deserializer;

public interface KafkaEventDeserializer<T> extends Deserializer<T> {

}
