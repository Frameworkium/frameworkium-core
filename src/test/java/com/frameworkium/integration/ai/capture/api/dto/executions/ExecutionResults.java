package com.frameworkium.integration.ai.capture.api.dto.executions;

import java.util.List;

/** Executions message */
public class ExecutionResults extends BaseDTO<ExecutionResults> {

    public List<Execution> results;
    public int total;
}
