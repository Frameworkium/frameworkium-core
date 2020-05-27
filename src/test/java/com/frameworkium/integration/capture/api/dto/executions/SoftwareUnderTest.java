package com.frameworkium.integration.capture.api.dto.executions;

import com.frameworkium.lite.api.dto.AbstractDTO;

public class SoftwareUnderTest extends AbstractDTO<SoftwareUnderTest> {

    public String name;
    public String version;

    public static SoftwareUnderTest newInstance() {
        SoftwareUnderTest sut = new SoftwareUnderTest();
        sut.name = "frameworkium-core";
        sut.version = "master";
        return sut;
    }

}
