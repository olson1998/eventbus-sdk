package com.olsonsolution.eventbus.domain.model;

import org.junit.jupiter.api.TestInfo;

import java.util.Set;

public record TestPayload(String testName, Set<String> tags) {

    public static TestPayload fromTestInfo(TestInfo testInfo) {
        return new TestPayload(testInfo.getDisplayName(), testInfo.getTags());
    }

}
