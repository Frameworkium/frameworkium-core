package com.frameworkium.core.ui.capture.model;

import com.frameworkium.core.common.properties.Property;

public class SoftwareUnderTest {

    private String name;
    private String version;

    public SoftwareUnderTest() {
        if (Property.SUT_NAME.isSpecified()) {
            this.name = Property.SUT_NAME.getValue();
        }
        if (Property.SUT_VERSION.isSpecified()) {
            this.version = Property.SUT_VERSION.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
