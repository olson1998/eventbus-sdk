package com.olsonsolution.eventbus.domain.port.stereotype;

public interface MemberType {

    String name();

    char getShortIdentifier();

    String getIdentifier();

    boolean isEqualTo(MemberType memberType);

}
