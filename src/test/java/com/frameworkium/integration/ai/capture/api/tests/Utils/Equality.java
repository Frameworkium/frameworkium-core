package com.frameworkium.integration.ai.capture.api.tests.Utils;


import com.frameworkium.integration.ai.capture.api.dto.executions.Execution;

import static com.google.common.truth.Truth.assertThat;

public class Equality {

    public static void checkExecutionInput(Execution expectedEx, Execution responseEx) {
        assertThat(responseEx.browser).isEqualTo(expectedEx.browser);
        assertThat(responseEx.softwareUnderTest).isEqualTo(expectedEx.softwareUnderTest);
        assertThat(responseEx.testID).isEqualTo(expectedEx.testID);
        assertThat(responseEx.nodeAddress).isEqualTo(expectedEx.nodeAddress);
        // check is has the default status of new
        assertThat(responseEx.currentStatus).isEqualTo("new");
    }

}
