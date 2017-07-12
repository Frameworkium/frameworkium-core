package com.frameworkium.integration.ai.capture.api.dto.executions;

/** Represents the Software under test message. */
public class SoftwareUnderTest extends BaseDTO<SoftwareUnderTest> {
    public String name;
    private String version;

    static SoftwareUnderTest newInstance() {
        SoftwareUnderTest sut = new SoftwareUnderTest();
        sut.name = "frameworkium-core";
        sut.version = "master";
        return sut;
    }

}
