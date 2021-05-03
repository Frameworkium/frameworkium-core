package com.frameworkium.core.common.reporting.jira.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.frameworkium.core.api.dto.AbstractDTO;

public class ProjectDto extends AbstractDTO<ProjectDto> {
  @JsonSerialize(using = ToStringSerializer.class)
  public Long id;
  public String key;
}
