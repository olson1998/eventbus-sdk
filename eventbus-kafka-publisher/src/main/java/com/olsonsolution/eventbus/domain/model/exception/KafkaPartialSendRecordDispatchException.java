package com.olsonsolution.eventbus.domain.model.exception;

import com.olsonsolution.eventbus.domain.port.stereotype.EventAcknowledgment;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.exception.EventDispatchException;
import lombok.Getter;

import java.util.Collection;

@Getter
public class KafkaPartialSendRecordDispatchException extends EventDispatchException {

    private static final String MESSAGE = "Event for destination=%s sent success rate: [%s/%s]";

    private final EventDestination destination;

    private final Collection<EventAcknowledgment> eventAcknowledgments;

    public KafkaPartialSendRecordDispatchException(EventDestination destination,
                                                   Collection<EventAcknowledgment> eventAcknowledgments,
                                                   Collection<EventDispatchException> dispatchExceptions) {
        super(writeMessage(destination, eventAcknowledgments, dispatchExceptions));
        this.destination = destination;
        this.eventAcknowledgments = eventAcknowledgments;
        dispatchExceptions.forEach(this::addSuppressed);
    }

    private static String writeMessage(EventDestination destination,
                                       Collection<EventAcknowledgment> eventAcknowledgments,
                                       Collection<EventDispatchException> dispatchExceptions) {
        int totalMessages = eventAcknowledgments.size() + dispatchExceptions.size();
        return MESSAGE.formatted(destination, eventAcknowledgments.size(), totalMessages);
    }

}
