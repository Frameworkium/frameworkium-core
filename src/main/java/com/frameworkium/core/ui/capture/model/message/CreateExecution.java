package com.frameworkium.core.ui.capture.model.message;

import com.frameworkium.core.ui.capture.model.Browser;
import com.frameworkium.core.ui.capture.model.SoftwareUnderTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreateExecution {

    private String testID;
    private Browser browser;
    private SoftwareUnderTest softwareUnderTest;
    private String nodeAddress;

    private transient final Logger logger = LogManager.getLogger(CreateExecution.class);

    public CreateExecution(String testID, String nodeAddress) {

        logger.debug("CreateExecution: testID='%s', nodeAddress='%s", testID, nodeAddress);
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
