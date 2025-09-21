package com.olsonsolution.eventbus.domain.port.stereotype;

import lombok.NonNull;

public interface Member {

    @NonNull
    MemberType getType();

    @NonNull
    String getName();

}
