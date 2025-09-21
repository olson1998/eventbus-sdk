package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.port.stereotype.Member;
import com.olsonsolution.eventbus.domain.port.stereotype.MemberType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StandardMember implements Member {

    private final MemberType type;

    private final String name;

}
