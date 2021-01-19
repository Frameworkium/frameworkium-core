package com.frameworkium.core.common.reporting.jira.dto.executionsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frameworkium.core.api.dto.AbstractDTO;
import com.frameworkium.core.common.reporting.jira.dto.status.StatusDto;

import java.time.LocalDate;
import java.util.List;

public class ExecutionDto extends AbstractDTO<ExecutionDto> {
    public Long id;
    public Long orderId;
    public Long cycleId;
    public String cycleName;
    public String issueId;
    public String issueKey;
    public String issueSummary;
    public List<String> labels;
    public String issueDescription;
    public String projectKey;
    public Long projectId;
    public String project;
    public Long projectAvatarId;
    public String priority;
    public List<String> components;
    public Long versionId;
    public String versionName;
    public StatusDto status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MMM/dd")
    public LocalDate executedOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy/MMM/dd")
    public LocalDate creationDate;
    public String comment;
    public String htmlComment;
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
