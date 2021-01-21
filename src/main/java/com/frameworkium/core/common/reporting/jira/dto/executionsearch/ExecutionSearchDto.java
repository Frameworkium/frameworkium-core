package com.frameworkium.core.common.reporting.jira.dto.executionsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.frameworkium.core.api.dto.AbstractDTO;
import com.frameworkium.core.common.reporting.jira.dto.execution.ExecutionLightDto;
import com.frameworkium.core.common.reporting.jira.dto.status.StatusDto;

import java.time.LocalDate;
import java.util.List;

public class ExecutionSearchDto extends AbstractDTO<ExecutionSearchDto> {
    @JsonUnwrapped
    public ExecutionLightDto executionLightDto;
    public String issueId;
    public String issueSummary;
    public List<String> labels;
    public String issueDescription;
    public String project;
    public Long projectAvatarId;
    public String priority;
    public List<String> components;
    public StatusDto status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MMM/dd")
    public LocalDate executedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MMM/dd")
    public LocalDate creationDate;
    public String executedBy;
    public String executedByUserName;
    public List<String> executionDefects;
    public List<String> stepDefects;
    public Long executionDefectCount;
    public Long stepDefectCount;
    public Long totalDefectCount;
    public String executedByDisplay;
    public String assignee;
    public String assigneeUserName;
    public String assigneeDisplay;
}
