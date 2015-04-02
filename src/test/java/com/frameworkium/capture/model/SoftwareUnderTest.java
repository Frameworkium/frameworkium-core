package com.frameworkium.capture.model;

import com.frameworkium.config.SystemProperty;

public class SoftwareUnderTest {

    private String name;
    private String version;

    public SoftwareUnderTest() {
        this.name = SystemProperty.SUT_NAME.getValue();
        this.version = SystemProperty.SUT_VERSION.getValue();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
