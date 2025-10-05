package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.MappingResult;
import org.apache.kafka.common.serialization.Deserializer;

public interface KafkaEventDeserializer<T> extends Deserializer<MappingResult<T>> {

}
