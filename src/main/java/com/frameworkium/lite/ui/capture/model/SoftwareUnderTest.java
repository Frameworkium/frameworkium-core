package com.frameworkium.lite.ui.capture.model;

import static com.frameworkium.lite.common.properties.Property.SUT_NAME;
import static com.frameworkium.lite.common.properties.Property.SUT_VERSION;

public class SoftwareUnderTest {

    public String name;
    public String version;

    /**
     * Software under test object.
     */
    public SoftwareUnderTest() {
        if (SUT_NAME.isSpecified()) {
            this.name = SUT_NAME.getValue();
        }
        if (SUT_VERSION.isSpecified()) {
            this.version = SUT_VERSION.getValue();
        }
    }
}
