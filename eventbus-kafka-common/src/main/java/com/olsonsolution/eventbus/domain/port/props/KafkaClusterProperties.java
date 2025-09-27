package com.olsonsolution.eventbus.domain.port.props;

import java.util.Properties;

public interface KafkaClusterProperties {

    Properties getProducer();

    Properties getConsumer();

}
