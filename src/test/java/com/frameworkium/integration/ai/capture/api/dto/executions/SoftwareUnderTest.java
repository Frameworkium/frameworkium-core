package com.frameworkium.integration.ai.capture.api.dto.executions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** Represents the Software under test message. */
public class SoftwareUnderTest extends BaseDTO {
    public String name;
    public String version;

    public static SoftwareUnderTest newInstance() {
        SoftwareUnderTest sut = new SoftwareUnderTest();
        sut.name = "frameworkium-core";
        sut.version = "master";
        return sut;
    }

}
