package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.model.exception.InvalidEventDestinationPatternException;
import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
@EqualsAndHashCode
public class StandardEventDestination implements EventDestination {

    private static final String EVENT_DESTINATION_PATTERN =
            "^[a-zA-Z0-9_-]+\\.[a-zA-Z]\\.[a-zA-Z0-9_-]+\\.[a-zA-Z]\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+$";

    @NonNull
    private final String product;

    @NonNull
    private final Member publisher;

    @NonNull
    private final Member subscriber;

    @NonNull
    private final String entity;

    @NonNull
    private final String command;

    public StandardEventDestination(@NonNull String product,
                                    @NonNull Member publisher,
                                    @NonNull Member subscriber,
                                    @NonNull String entity,
                                    @NonNull String command) {
        this.product = product;
        String[] values = new String[]{
                String.valueOf(publisher.getType().getIdentifier()),
                publisher.getName(),
                String.valueOf(subscriber.getType().getIdentifier()),
                subscriber.getName(),
                entity,
                command
        };
        validateDestination(String.join(".", values));
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.entity = entity;
        this.command = command;
    }

    public static StandardEventDestination fromString(String destinationString) {
        validateDestination(destinationString);
        String[] values = StringUtils.split(destinationString, '.');
        return StandardEventDestination.builder()
                .product(values[0])
                .publisher(StandardMember.builder()
                        .type(MemberTypes.fromIdentifier(extractIdentifier(values[1])))
                        .name(values[2])
                        .build())
                .subscriber(StandardMember.builder()
                        .type(MemberTypes.fromIdentifier(extractIdentifier(values[3])))
                        .name(values[4])
                        .build())
                .entity(values[5])
                .command(values[6])
                .build();
    }

    private static void validateDestination(String destinationString) {
        if (!destinationString.matches(EVENT_DESTINATION_PATTERN)) {
            throw new InvalidEventDestinationPatternException(destinationString);
        }
    }

    private static char extractIdentifier(String value) {
        return value.charAt(0);
    }

    @Override
    public String toString() {
        return String.join(".",
                String.valueOf(publisher.getType().getIdentifier()),
                publisher.getName(),
                String.valueOf(subscriber.getType().getIdentifier()),
                subscriber.getName(),
                entity,
                command
        );
    }

}
