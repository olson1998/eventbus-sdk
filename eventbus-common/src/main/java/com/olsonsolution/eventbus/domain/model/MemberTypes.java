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

    ALL('A'),
    FIRST('F'),
    NODE('N'),
    NODE_GROUP('G'),
    TENANT('T');

    private final char identifier;

    public static MemberType fromIdentifier(char identifier) {
        return Arrays.stream(values())
                .filter(memberType -> memberType.getIdentifier() == identifier)
                .findFirst()
                .orElseThrow(() -> new UnknownMemberTypeException(identifier));
    }

}
