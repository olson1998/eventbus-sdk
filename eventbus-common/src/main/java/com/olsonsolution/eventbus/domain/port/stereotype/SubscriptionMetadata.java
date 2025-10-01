package com.olsonsolution.eventbus.domain.port.stereotype;

import com.asyncapi.v3._0_0.model.AsyncAPI;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface SubscriptionMetadata {

    UUID getId();

    ZonedDateTime getExpireAt();

    ZonedDateTime getCreatedAt();

    AsyncAPI getApiDocs();

}
