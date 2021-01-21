package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.frameworkium.core.api.dto.AbstractDTO;

public class ExecutionLightDto extends AbstractDTO<ExecutionLightDto> {
    public Long id;
    public Long orderId;
    public String comment;
    public String htmlComment;
    public Long cycleId;
    public String cycleName;
    public Long versionId;
    public String versionName;
    public Long projectId;
    public String issueKey;
    public String projectKey;
}
