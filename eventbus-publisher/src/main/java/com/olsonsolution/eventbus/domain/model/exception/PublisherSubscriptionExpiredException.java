package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.repository.publisher.subscription.PublisherSubscription;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import lombok.Getter;

@Getter
public class PublisherSubscriptionExpiredException extends EventDispatchException {

    private static final String MESSAGE = "Publisher subscription expired for destination=%s";

    private final PublisherSubscription<?> publisherSubscription;

    public PublisherSubscriptionExpiredException(PublisherSubscription<?> publisherSubscription) {
        super(MESSAGE.formatted(publisherSubscription.getDestination()));
        this.publisherSubscription = publisherSubscription;
    }
}
