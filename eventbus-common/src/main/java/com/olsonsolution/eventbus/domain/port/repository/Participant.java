package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;

public interface Participant extends AutoCloseable {

    boolean isClosed();

    void register();

    void unregister();

    void onDestinationDestroyed(EventDestination destination);

}
