package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.CorruptedEventMessage;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StandardCorruptedEventMessage<T> extends StandardEventMessage<T> implements CorruptedEventMessage<T> {

    private final Throwable corruptionCause;

    @Builder(builderMethodName = "corruptedEventBuilder")
    public StandardCorruptedEventMessage(Map<String, Object> headers,
                                         ZonedDateTime timestamp,
                                         Throwable corruptionCause) {
        super(null, headers, timestamp);
        this.corruptionCause = corruptionCause;
    }
}
