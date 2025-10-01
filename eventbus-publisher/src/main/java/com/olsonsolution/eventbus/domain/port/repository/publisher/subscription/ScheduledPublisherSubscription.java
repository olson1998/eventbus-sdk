package com.olsonsolution.eventbus.domain.port.repository.publisher.subscription;

import java.time.Duration;

public interface ScheduledPublisherSubscription extends PublisherSubscription {

    Duration getDispatchDuration();

}
