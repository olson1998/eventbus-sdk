package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.EventDestination;
import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class StandardEventDestination implements EventDestination {

    private static final String EVENT_DESTINATION_PATTERN =
            "^(?=.{1,249}$)[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+){5}$";

    @NonNull
    private final String product;

    @NonNull
    private final Member subscriber;

    @NonNull
    private final String entity;

    @NonNull
    private final String command;

    public StandardEventDestination(@NonNull String product,
                                    @NonNull Member subscriber,
                                    @NonNull String entity,
                                    @NonNull String command) {
        this.product = product;
        this.subscriber = subscriber;
        this.entity = entity;
        this.command = command;
    }

//    public static StandardEventDestination fromString(String destinationString) {
//        validateDestination(destinationString);
//        String[] values = StringUtils.split(destinationString, '.');
//        return StandardEventDestination.builder()
//                .product(values[0])
//                .publisher(StandardMember.builder()
//                        .type(MemberTypes.fromIdentifier(extractIdentifier(values[1])))
//                        .name(values[2])
//                        .build())
//                .subscriber(StandardMember.builder()
//                        .type(MemberTypes.fromIdentifier(extractIdentifier(values[3])))
//                        .name(values[4])
//                        .build())
//                .entity(values[5])
//                .command(values[6])
//                .build();
//    }
//
//    private static void validateDestination(String destinationString) {
//        if (!destinationString.matches(EVENT_DESTINATION_PATTERN)) {
//            throw new InvalidEventDestinationPatternException(destinationString);
//        }
//    }
//
//    private static char extractIdentifier(String value) {
//        return value.charAt(0);
//    }

    @Override
    public String toString() {
        return "sub-channel" + String.join(".",
                String.valueOf(subscriber.getType().getIdentifier()),
                subscriber.getName(),
                entity,
                command
        );
    }

}
