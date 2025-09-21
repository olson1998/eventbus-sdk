package com.olsonsolution.eventbus.domain.port.stereotype;

import java.time.ZonedDateTime;

public interface EventAcknowledgment {

    ZonedDateTime getTimestamp();

}
