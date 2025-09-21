package com.olsonsolution.eventbus.domain.port.repository;

public interface Participant {

    Subscription<?> getSubscription();

    void subscribe();

}
