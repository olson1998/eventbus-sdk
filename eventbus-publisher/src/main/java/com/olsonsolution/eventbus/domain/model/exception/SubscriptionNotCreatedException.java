package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import lombok.Getter;

@Getter
public class SubscriptionNotCreatedException extends EventDispatchException {

    private static final String MESSAGE = "Subscription not created for destination=%s";

    private final EventChannel destination;

    public SubscriptionNotCreatedException(EventChannel destination) {
        super(MESSAGE.formatted(destination));
        this.destination = destination;
    }
}
