package com.olsonsolution.eventbus.domain.model;

import com.olsonsolution.eventbus.domain.model.exception.UnknownMemberTypeException;
import com.olsonsolution.eventbus.domain.port.stereotype.MemberType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MemberTypes implements MemberType {

    ALL("all", 'A'),
    FIRST_WINS("first_wins", 'F'),
    NODE("node", 'N'),
    GROUP("node_group", 'G'),
    TENANT("tenant", 'T');

    private final String identifier;
    private final char shortIdentifier;

    public static MemberType fromIdentifier(char identifier) {
        return Arrays.stream(values())
                .filter(memberType -> memberType.getShortIdentifier() == identifier)
                .findFirst()
                .orElseThrow(() -> new UnknownMemberTypeException(identifier));
    }

    @Override
    public boolean isEqualTo(MemberType memberType) {
        if (memberType instanceof MemberTypes memberTypes) {
            return memberTypes.equals(this);
        } else if (memberType != null) {
            return name().equals(memberType.name()) && identifier == memberType.getIdentifier();
        }
        return false;
    }
}
