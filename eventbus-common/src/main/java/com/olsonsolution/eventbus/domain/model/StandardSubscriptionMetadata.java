package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.SubscriptionMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardSubscriptionMetadata implements SubscriptionMetadata {

    private UUID id;

    private ZonedDateTime expireAt;

    private ZonedDateTime createdAt;

}
