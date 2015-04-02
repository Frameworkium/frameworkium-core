package com.frameworkium.capture.model.message;

import com.frameworkium.capture.model.Browser;
import com.frameworkium.capture.model.SoftwareUnderTest;

public final class CreateExecution {

    private String testID;
    private Browser browser;
    private SoftwareUnderTest softwareUnderTest;
    private String nodeAddress;

    public CreateExecution(String testID, String nodeAddress) {

        this.testID = testID;
        this.browser = new Browser();
        this.softwareUnderTest = new SoftwareUnderTest();
        this.nodeAddress = nodeAddress;
    }

    public String getTestID() {
        return testID;
    }

    public Browser getBrowser() {
        return browser;
    }

    public SoftwareUnderTest getSoftwareUnderTest() {
        return softwareUnderTest;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }
}
