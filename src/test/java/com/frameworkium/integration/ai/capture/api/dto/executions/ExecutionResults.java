package com.frameworkium.integration.ai.capture.api.dto.executions;

import com.frameworkium.core.api.dto.AbstractDTO;

import java.util.List;

public class ExecutionResults extends AbstractDTO {

    public List<Execution> results;
    public int total;
}
