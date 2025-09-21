package com.olsonsolution.eventbus.domain.port.stereotype;

import java.time.ZonedDateTime;
import java.util.Map;

public interface EventMessage<C> {

    C getContent();

    ZonedDateTime getTimestamp();

    Map<String, Object> getHeaders();

}
