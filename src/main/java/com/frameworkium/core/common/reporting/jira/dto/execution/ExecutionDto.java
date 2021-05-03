package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonDeserialize(builder = ExecutionDto.Builder.class)
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

  private ExecutionDto(Builder builder) {
    this.executionLightDto = builder.executionLightDto;
    this.executionStatus = builder.executionStatus;
    this.createdBy = builder.createdBy;
    this.modifiedBy = builder.modifiedBy;
    this.issueId = builder.issueId;
    this.summary = builder.summary;
    this.label = builder.label;
    this.component = builder.component;
  }

  public static ExecutionDto.Builder newBuilder() {
    return new ExecutionDto.Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    private ExecutionLightDto executionLightDto;
    private String executionStatus;
    private String createdBy;
    private String modifiedBy;
    private Long issueId;
    private String summary;
    private String label;
    private String component;

    private Builder() {
    }

    public Builder executionLightDto(ExecutionLightDto executionLightDto) {
      this.executionLightDto = executionLightDto;
      return this;
    }

    public Builder executionStatus(String executionStatus) {
      this.executionStatus = executionStatus;
      return this;
    }

    public Builder createdBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder modifiedBy(String modifiedBy) {
      this.modifiedBy = modifiedBy;
      return this;
    }

    public Builder issueId(Long issueId) {
      this.issueId = issueId;
      return this;
    }

    public Builder summary(String summary) {
      this.summary = summary;
      return this;
    }

    public Builder label(String label) {
      this.label = label;
      return this;
    }

    public Builder component(String component) {
      this.component = component;
      return this;
    }

    public ExecutionDto build() {
      return new ExecutionDto(this);
    }
  }
}
