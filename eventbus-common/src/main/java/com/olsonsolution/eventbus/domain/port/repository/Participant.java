package com.olsonsolution.eventbus.domain.port.repository;

import com.olsonsolution.eventbus.domain.port.stereotype.EventChannel;

public interface Participant extends AutoCloseable {

    boolean isClosed();

    void register();

    void unregister();

    void onDestinationDestroyed(EventChannel destination);

}
