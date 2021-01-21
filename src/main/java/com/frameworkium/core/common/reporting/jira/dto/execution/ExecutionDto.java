package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.frameworkium.core.api.dto.AbstractDTO;

import java.time.LocalDate;

public class ExecutionDto extends AbstractDTO<ExecutionDto> {
    @JsonUnwrapped
    public ExecutionLightDto executionLightDto;
    public String executionStatus;
    public String createdBy;
    public String modifiedBy;
    public Long issueId;
    public String summary;
    public String label;
    public String component;
}
