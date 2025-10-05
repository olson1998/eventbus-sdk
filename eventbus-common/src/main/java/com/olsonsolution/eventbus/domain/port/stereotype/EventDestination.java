package com.olsonsolution.eventbus.domain.port.stereotype;

public interface EventDestination {

    Member getSubscriber();

    String getEntity();

    String getCommand();

}
