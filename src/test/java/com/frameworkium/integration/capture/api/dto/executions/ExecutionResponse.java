package com.frameworkium.integration.capture.api.dto.executions;

import com.frameworkium.core.api.dto.AbstractDTO;
import com.frameworkium.integration.capture.api.dto.screenshots.Screenshot;

import java.util.List;

public class ExecutionResponse extends AbstractDTO<ExecutionResponse> {

    public String testID;
    public Browser browser;
    public SoftwareUnderTest softwareUnderTest;
    public String nodeAddress;
    public String created;
    public String lastUpdated;
    public String currentStatus;
    public String executionID;
    public List<Screenshot> screenshots;

    public boolean createdFrom(CreateExecution createMessage) {
        return browser.equals(createMessage.browser)
                && softwareUnderTest.equals(createMessage.softwareUnderTest)
                && testID.equals(createMessage.testID)
                && nodeAddress.equals(createMessage.nodeAddress);
    }

}
