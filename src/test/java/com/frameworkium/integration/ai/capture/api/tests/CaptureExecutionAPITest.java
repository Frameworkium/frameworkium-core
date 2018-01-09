package com.frameworkium.integration.ai.capture.api.tests;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.frameworkium.integration.ai.capture.api.dto.executions.*;
import com.frameworkium.integration.ai.capture.api.dto.screenshots.CreateScreenshot;
import com.frameworkium.integration.ai.capture.api.dto.screenshots.Screenshot;
import com.frameworkium.integration.ai.capture.api.service.executions.ExecutionService;
import com.frameworkium.integration.ai.capture.api.service.screenshots.ScreenshotService;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

/** Tests for the Capture execution API. */
public class CaptureExecutionAPITest extends BaseAPITest {

    @Test
    public void create_execution_appears_in_results() {
        ExecutionService executionService = new ExecutionService();
        CreateExecution createExMessage = CreateExecution.newCreateInstance();
        String id = executionService
                .createExecution(createExMessage)
                .executionID;

        ExecutionResults latestExecutions = executionService.getExecutions(1, 10);

        List<ExecutionResponse> filteredExecutions = latestExecutions.results.stream()
                .filter(ex -> id.equals(ex.executionID))
                .collect(Collectors.toList());

        // ensure only one with our expected ID
        assertThat(filteredExecutions).hasSize(1);

        ExecutionResponse response = filteredExecutions.get(0);
        assertThat(response.createdFrom(createExMessage)).isTrue();
        assertThat(response.currentStatus).isEqualTo("new");

        // check total is at least the number of results returns
        assertThat(latestExecutions.total)
                .isAtLeast(latestExecutions.results.size());
    }

    @Test
    public void can_add_then_view_screenshot() {
        ExecutionService executionService = new ExecutionService();
        CreateExecution createExMessage = CreateExecution.newCreateInstance();
        String executionID = executionService
                .createExecution(createExMessage)
                .executionID;

        ScreenshotService screenshotService = new ScreenshotService();
        CreateScreenshot createScreenshot = CreateScreenshot.newInstance(executionID);
        screenshotService.createScreenshot(createScreenshot);

        List<Screenshot> screenshots = executionService.getExecution(executionID).screenshots;
        assertThat(screenshots).isNotEmpty();
        Screenshot returnedScreenshot = screenshots.get(0);
        assertThat(returnedScreenshot.command).isEqualTo(createScreenshot.command);
        assertThat(returnedScreenshot.imageURL).endsWith(".png");
    }
}
