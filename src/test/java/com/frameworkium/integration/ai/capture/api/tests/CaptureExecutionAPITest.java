package com.frameworkium.integration.ai.capture.api.tests;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.ai.capture.api.dto.executions.*;
import com.frameworkium.integration.ai.capture.api.service.executions.ExecutionService;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

/** Tests for the Capture execution API. */
public class CaptureExecutionAPITest extends BaseTest {

    @Test(enabled = false)
    public void create_execution_returns_id() {
        Execution execution = Execution.newCreateInstance();
        ExecutionID id = new ExecutionService().createExecution(execution);
        assertThat(id.executionID).isNotEmpty();
    }

    @Test(enabled = false)
    public void create_execution_appears_in_results() {
        final ExecutionService executionService = new ExecutionService();
        // create execution
        final Execution createMessage = Execution.newCreateInstance();
        String id = executionService
                .createExecution(createMessage)
                .executionID;
        // get latest executions
        ExecutionResults latestExecutions = executionService.getExecutions(1, 10);

        List<Execution> filteredExecutions = latestExecutions.results.stream()
                .filter(ex -> id.equals(ex.executionID))
                .collect(Collectors.toList());

        // ensure only one with our expected ID
        assertThat(filteredExecutions).hasSize(1);

        // check it matches input
        Execution ex = filteredExecutions.get(0);
        assertThat(ex.browser).isEqualTo(createMessage.browser);
        assertThat(ex.softwareUnderTest).isEqualTo(createMessage.softwareUnderTest);
        assertThat(ex.testID).isEqualTo(createMessage.testID);
        assertThat(ex.nodeAddress).isEqualTo(createMessage.nodeAddress);
        // check is has the default status of new
        assertThat(ex.currentStatus).isEqualTo("new");

        // check total is at least the number of results returns
        assertThat(latestExecutions.total)
                .isAtLeast(latestExecutions.results.size());
    }
}
