package com.frameworkium.integration.ai.capture.api.service.executions;

import com.frameworkium.integration.ai.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.ai.capture.api.dto.executions.*;
import com.frameworkium.integration.ai.capture.api.service.BaseCaptureService;
import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpStatus;
import ru.yandex.qatools.allure.annotations.Step;

/** Encapsulates the Capture ExecutionResponse service */
public class ExecutionService extends BaseCaptureService {

    @Step("Create Capture ExecutionResponse {0}")
    public ExecutionID createExecution(CreateExecution createMessage) {

        return getRequestSpec()
                .when()
                .body(createMessage)
                .post(CaptureEndpoint.EXECUTIONS.getUrl())
                .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(ExecutionID.class);
    }

    @Step("Get Capture Executions, page={0}, pageSize={1}")
    public ExecutionResults getExecutions(int page, int pageSize) {
        return request(
                ImmutableMap.of("page", page, "pageSize", pageSize),
                CaptureEndpoint.EXECUTIONS.getUrl())
                .as(ExecutionResults.class);
    }

    public ExecutionResponse getExecution(String executionID) {
        return get(CaptureEndpoint.GET_EXECUTION.getUrl(executionID))
                .as(ExecutionResponse.class);
    }
}
