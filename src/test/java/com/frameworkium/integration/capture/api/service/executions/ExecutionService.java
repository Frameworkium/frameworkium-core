package com.frameworkium.integration.capture.api.service.executions;

import com.frameworkium.integration.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.capture.api.dto.executions.*;
import com.frameworkium.integration.capture.api.service.BaseCaptureService;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Step;

/** Encapsulates the Capture ExecutionResponse service */
public class ExecutionService extends BaseCaptureService {

    @Step("Create Capture Execution {createMessage}")
    public ExecutionID createExecution(CreateExecution createMessage) {
        return post(CaptureEndpoint.EXECUTIONS.getUrl(), createMessage)
                .extract()
                .as(ExecutionID.class);
    }

    @Step("Get Capture Executions, page={page}, pageSize={pageSize}")
    public ExecutionResults getExecutions(int page, int pageSize) {
        return get(
                ImmutableMap.of("page", page, "pageSize", pageSize),
                CaptureEndpoint.EXECUTIONS.getUrl())
                .as(ExecutionResults.class);
    }

    public ExecutionResponse getExecution(String executionID) {
        return get(CaptureEndpoint.GET_EXECUTION.getUrl(executionID))
                .as(ExecutionResponse.class);
    }
}
