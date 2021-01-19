package com.frameworkium.core.common.reporting.jira.dto.executionsearch;

import com.frameworkium.core.api.dto.AbstractDTO;

import java.util.List;

public class ExecutionSearchDto extends AbstractDTO<ExecutionSearchDto> {
    public List<ExecutionDto> executions;
    public Long currentIndex;
    public Long maxResultAllowed;
    public List<Long> linksNew;
    public Long totalCount;
    public List<Long> executionIds;
}
