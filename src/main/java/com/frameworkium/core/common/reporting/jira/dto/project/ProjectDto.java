package com.frameworkium.core.common.reporting.jira.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.frameworkium.core.api.dto.AbstractDTO;

public class ProjectDto extends AbstractDTO<ProjectDto> {
    @JsonSerialize(as = String.class)
    public Long id;
    public String key;
    //truncated stuff i don't care about
}
