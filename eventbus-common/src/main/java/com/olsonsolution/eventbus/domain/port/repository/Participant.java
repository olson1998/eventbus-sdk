package com.olsonsolution.eventbus.domain.port.repository;

public interface Participant<S extends Subscription> {

    S getSubscription();

}
