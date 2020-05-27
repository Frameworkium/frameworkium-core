package com.frameworkium.integration.capture.api.dto.executions;

import com.frameworkium.lite.api.dto.AbstractDTO;

import java.util.List;

public class ExecutionResults extends AbstractDTO<ExecutionResults> {

    public List<ExecutionResponse> results;
    public int total;
}
