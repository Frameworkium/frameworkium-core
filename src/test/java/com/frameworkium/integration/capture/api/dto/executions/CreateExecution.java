package com.frameworkium.integration.capture.api.dto.executions;

import com.frameworkium.core.api.dto.AbstractDTO;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CreateExecution extends AbstractDTO<CreateExecution> {

    public String testID;
    public Browser browser;
    public SoftwareUnderTest softwareUnderTest;
    public String nodeAddress;

    /**
     * Using the static factory method pattern instead of using a constructor
     * which allows for multiple different, well named, creation methods.
     *
     * @return a populated {@link CreateExecution} with sensible defaults.
     */
    public static CreateExecution newCreateInstance() {
        CreateExecution execution = new CreateExecution();
        execution.testID = "DEFAULT-1";
        execution.browser = Browser.newInstance();
        execution.softwareUnderTest = SoftwareUnderTest.newInstance();
        try {
            execution.nodeAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            execution.nodeAddress = null;
        }
        return execution;
    }
}
