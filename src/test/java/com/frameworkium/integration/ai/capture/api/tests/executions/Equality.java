package com.frameworkium.integration.ai.capture.api.tests.executions;


import com.frameworkium.integration.ai.capture.api.dto.executions.Execution;

import static com.google.common.truth.Truth.assertThat;

public class Equality {

    /**
     * Checks that the input values of the response match the
     * input values of what you expect to be in the response.
     * @param expectedEx Your expected response
     * @param responseEx Your actual response
     */
    public static void checkExecutionInput(Execution expectedEx, Execution responseEx) {
        assertThat(responseEx.browser).isEqualTo(expectedEx.browser);
        assertThat(responseEx.softwareUnderTest).isEqualTo(expectedEx.softwareUnderTest);
        assertThat(responseEx.testID).isEqualTo(expectedEx.testID);
        assertThat(responseEx.nodeAddress).isEqualTo(expectedEx.nodeAddress);
        // check is has the default status of new
        assertThat(responseEx.currentStatus).isEqualTo("new");
    }

}
