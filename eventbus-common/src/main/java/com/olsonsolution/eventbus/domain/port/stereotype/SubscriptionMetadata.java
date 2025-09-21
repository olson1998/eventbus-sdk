package com.olsonsolution.eventbus.domain.port.stereotype;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface SubscriptionMetadata {

    UUID getId();

    ZonedDateTime getExpireAt();

    ZonedDateTime getCreatedAt();

}
