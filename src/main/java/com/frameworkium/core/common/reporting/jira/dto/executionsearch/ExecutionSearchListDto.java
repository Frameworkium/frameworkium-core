package com.frameworkium.core.common.reporting.jira.dto.executionsearch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;
import java.util.List;

@JsonDeserialize(builder = ExecutionSearchListDto.Builder.class)
public class ExecutionSearchListDto extends AbstractDTO<ExecutionSearchListDto> {
  public List<ExecutionSearchDto> executions;
  public Long currentIndex;
  public Long maxResultAllowed;
  public List<Long> linksNew;
  public Long totalCount;
  public List<Long> executionIds;

  public ExecutionSearchListDto(Builder builder) {
    this.executions = builder.executions;
    this.currentIndex = builder.currentIndex;
    this.maxResultAllowed = builder.maxResultAllowed;
    this.linksNew = builder.linksNew;
    this.totalCount = builder.totalCount;
    this.executionIds = builder.executionIds;
  }

  public static ExecutionSearchListDto.Builder newBuilder() {
    return new ExecutionSearchListDto.Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    private List<ExecutionSearchDto> executions;
    private Long currentIndex;
    private Long maxResultAllowed;
    private List<Long> linksNew;
    private Long totalCount;
    private List<Long> executionIds;

    public Builder executions(final List<ExecutionSearchDto> executions) {
      this.executions = executions;
      return this;
    }

    public Builder currentIndex(final Long currentIndex) {
      this.currentIndex = currentIndex;
      return this;
    }

    public Builder maxResultAllowed(final Long maxResultAllowed) {
      this.maxResultAllowed = maxResultAllowed;
      return this;
    }

    public Builder linksNew(final List<Long> linksNew) {
      this.linksNew = linksNew;
      return this;
    }

    public Builder totalCount(final Long totalCount) {
      this.totalCount = totalCount;
      return this;
    }

    public Builder executionIds(final List<Long> executionIds) {
      this.executionIds = executionIds;
      return this;
    }

    public ExecutionSearchListDto build() {
      return new ExecutionSearchListDto(this);
    }
  }
}
