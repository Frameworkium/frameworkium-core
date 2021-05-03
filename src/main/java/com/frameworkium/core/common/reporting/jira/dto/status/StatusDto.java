package com.frameworkium.core.common.reporting.jira.dto.status;

import com.frameworkium.core.api.dto.AbstractDTO;

public class StatusDto extends AbstractDTO<StatusDto> {
  public Long id;
  public String name;
  public String description;
  public String color;
  public Long type;
}
