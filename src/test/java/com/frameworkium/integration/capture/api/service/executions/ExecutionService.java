package com.frameworkium.integration.capture.api.service.executions;

import com.frameworkium.integration.capture.api.constant.CaptureEndpoint;
import com.frameworkium.integration.capture.api.dto.executions.*;
import com.frameworkium.integration.capture.api.service.BaseCaptureService;
import com.google.common.collect.ImmutableMap;

/** Encapsulates the Capture ExecutionResponse service */
public class ExecutionService extends BaseCaptureService {

    public ExecutionID createExecution(CreateExecution createMessage) {
        return post(CaptureEndpoint.EXECUTIONS.getUrl(), createMessage)
                .extract()
                .as(ExecutionID.class);
    }

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
