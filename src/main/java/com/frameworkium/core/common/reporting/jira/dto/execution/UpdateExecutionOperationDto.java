package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonDeserialize(builder = UpdateExecutionOperationDto.Builder.class)
public class UpdateExecutionOperationDto extends AbstractDTO<UpdateExecutionOperationDto> {
  @JsonUnwrapped
  public ExecutionDto executionDto;
  public String executedOn;
  public String executionBy;
  public String executedByDisplay;
  public Integer status;

  private UpdateExecutionOperationDto(Builder builder) {
    this.executionBy = builder.executionBy;
    this.executionDto = builder.executionDto;
    this.executedOn = builder.executedOn;
    this.executedByDisplay = builder.executedByDisplay;
    this.status = builder.status;
  }

  public static UpdateExecutionOperationDto.Builder newBuilder() {
    return new UpdateExecutionOperationDto.Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    private ExecutionDto executionDto;
    private String executedOn;
    private String executionBy;
    private String executedByDisplay;
    private Integer status;

    private Builder() {
    }

    public Builder executionDto(ExecutionDto executionDto) {
      this.executionDto = executionDto;
      return this;
    }

    public Builder executedOn(String executedOn) {
      this.executedOn = executedOn;
      return this;
    }

    public Builder executionBy(String executionBy) {
      this.executionBy = executionBy;
      return this;
    }

    public Builder executedByDisplay(String executedByDisplay) {
      this.executedByDisplay = executedByDisplay;
      return this;
    }

    public Builder status(Integer status) {
      this.status = status;
      return this;
    }

    public UpdateExecutionOperationDto build() {
      return new UpdateExecutionOperationDto(this);
    }
  }
}
