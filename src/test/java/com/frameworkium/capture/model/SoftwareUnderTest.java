package com.frameworkium.capture.model;

import com.frameworkium.config.SystemProperty;

public class SoftwareUnderTest {

    private String name;
    private String version;

    public SoftwareUnderTest() {
        if (SystemProperty.SUT_NAME.isSpecified()) {
            this.name = SystemProperty.SUT_NAME.getValue();
        }
        if (SystemProperty.SUT_VERSION.isSpecified()) {
            this.version = SystemProperty.SUT_VERSION.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
