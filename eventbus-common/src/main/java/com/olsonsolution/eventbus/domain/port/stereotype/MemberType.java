package com.olsonsolution.eventbus.domain.port.stereotype;

public interface MemberType {

    String name();

    char getIdentifier();

    boolean isEqualTo(MemberType memberType);

}
