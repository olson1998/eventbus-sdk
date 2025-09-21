package com.olsonsolution.eventbus.domain.port.stereotype;

public interface EventDestination {

    Member getPublisher();

    Member getSubscriber();

    String getEntity();

    String getCommand();

}
