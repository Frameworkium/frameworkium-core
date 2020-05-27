package com.frameworkium.integration.capture.api.tests;

import com.frameworkium.lite.api.tests.BaseAPITest;
import com.frameworkium.integration.capture.api.dto.executions.*;
import com.frameworkium.integration.capture.api.dto.screenshots.CreateScreenshot;
import com.frameworkium.integration.capture.api.dto.screenshots.Screenshot;
import com.frameworkium.integration.capture.api.service.executions.ExecutionService;
import com.frameworkium.integration.capture.api.service.screenshots.ScreenshotService;
import org.openqa.selenium.NotFoundException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests for the Capture execution API. */
@Ignore
public class CaptureExecutionAPITest extends BaseAPITest {

    private CreateExecution createExMessage;
    private String executionID;

    /**
     * Using {@link BeforeClass} to ensure anything like:
     * https://github.com/cbeust/testng/issues/1660
     * gets caught before we release.
     * This, with threads, is a common pattern.
     */
    @BeforeClass
    public void create_execution() {
        createExMessage = CreateExecution.newCreateInstance();
        executionID = new ExecutionService()
                .createExecution(createExMessage)
                .executionID;
    }

    @Test
    public void execution_appears_in_results() {
        ExecutionResults latestExecutions =
                new ExecutionService().getExecutions(1, 10);

        assertThat(latestExecutions.total)
                .isGreaterThanOrEqualTo(latestExecutions.results.size());

        List<ExecutionResponse> filteredExecutions =
                latestExecutions
                        .results.stream()
                        .filter(ex -> executionID.equals(ex.executionID))
                        .collect(Collectors.toList());

        // ensure only one with our expected ID
        assertThat(filteredExecutions).hasSize(1);

        ExecutionResponse response = filteredExecutions.get(0);
        assertThat(response.createdFrom(createExMessage)).isTrue();
    }

    @Test
    public void new_execution_has_status_new_and_last_updated_equals_created() {
        String id = new ExecutionService()
                .createExecution(createExMessage)
                .executionID;
        ExecutionResponse execution = new ExecutionService()
                .getExecutions(1, 20)
                .results.stream()
                .filter(ex -> id.equals(ex.executionID))
                .findFirst().orElseThrow(NotFoundException::new);
        assertThat(execution.currentStatus).isEqualTo("new");
        assertThat(execution.lastUpdated).isEqualTo(execution.created);
    }

    @Test
    public void can_add_then_view_screenshot() {
        CreateScreenshot createScreenshot = CreateScreenshot.newInstance(executionID);
        new ScreenshotService().createScreenshot(createScreenshot);

        Screenshot returnedScreenshot =
                new ExecutionService().getExecution(executionID).screenshots.get(0);
        assertThat(returnedScreenshot.command).isEqualTo(createScreenshot.command);
        assertThat(returnedScreenshot.imageURL).endsWith(".png");
    }
}
