package com.olsonsolution.eventbus.domain.port.stereotype;

public interface EventChannel {

    Member getSubscriber();

    String getEntity();

    String getCommand();

}
