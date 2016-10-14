package com.frameworkium.integration.ai.capture.api.dto.executions;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** Execution message. */
public class Execution {

    // used in the create
    public String testID;
    public Browser browser;
    public SoftwareUnderTest softwareUnderTest;
    public String nodeAddress;

    // fields in the response
    public String created;
    public String lastUpdated;
    public String currentStatus;
    public String executionID;

    /**
     * Using the static factory method pattern instead of using the constructor
     * to ensure this class can be used for both input and output. Also allows
     * for multiple different creation methods in one place.
     *
     * @return a populated {@link Execution} with sensible defaults.
     */
    public static Execution newCreateInstance() {
        Execution execution = new Execution();
        execution.testID = "Default";
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
