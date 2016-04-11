package com.frameworkium.core.ui.capture.model;

import com.frameworkium.core.common.properties.CommonProperty;

public class SoftwareUnderTest {

    private String name;
    private String version;

    public SoftwareUnderTest() {
        if (CommonProperty.SUT_NAME.isSpecified()) {
            this.name = CommonProperty.SUT_NAME.getValue();
        }
        if (CommonProperty.SUT_VERSION.isSpecified()) {
            this.version = CommonProperty.SUT_VERSION.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
