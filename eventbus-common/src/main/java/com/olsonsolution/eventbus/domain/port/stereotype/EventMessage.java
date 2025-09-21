package com.olsonsolution.eventbus.domain.port.stereotype;

import java.time.ZonedDateTime;
import java.util.Map;

public interface EventMessage<C, D extends EventDestination> {

    C getContent();

    ZonedDateTime getTimestamp();

    D getDestination();

    Map<String, String> getHeaders();

}
