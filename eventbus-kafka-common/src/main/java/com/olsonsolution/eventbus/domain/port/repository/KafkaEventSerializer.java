package com.olsonsolution.eventbus.domain.port.repository;

import org.apache.kafka.common.serialization.Serializer;

public interface KafkaEventSerializer<T> extends Serializer<T> {
}
