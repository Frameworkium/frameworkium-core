package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.frameworkium.core.api.dto.AbstractDTO;

public class UpdateExecutionOperationDto extends AbstractDTO<UpdateExecutionOperationDto> {
    @JsonUnwrapped
    public ExecutionDto executionDto;
    public String executedOn;
    public String executionBy;
    public String executedByDisplay;
    public Integer status;
}
