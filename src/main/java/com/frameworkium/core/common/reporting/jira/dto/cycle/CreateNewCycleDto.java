package com.frameworkium.core.common.reporting.jira.dto.cycle;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonDeserialize(builder = CreateNewCycleDto.Builder.class)
public class CreateNewCycleDto extends AbstractDTO<CreateNewCycleDto> {
  @JsonUnwrapped
  public CycleLightDto cycleLightDto;
  public String cloneCycleId;
  public String sprintId;

  private CreateNewCycleDto(final Builder builder) {
    cycleLightDto = builder.cycleLightDto;
    cloneCycleId = builder.cloneCycleId;
    sprintId = builder.sprintId;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {
    @JsonUnwrapped
    private CycleLightDto cycleLightDto;
    private String cloneCycleId;
    private String sprintId;

    private Builder() {
    }

    public Builder cycleLightDto(final CycleLightDto cycleLightDto) {
      this.cycleLightDto = cycleLightDto;
      return this;
    }

    public Builder cloneCycleId(final String cloneCycleId) {
      this.cloneCycleId = cloneCycleId;
      return this;
    }

    public Builder sprintId(final String sprintId) {
      this.sprintId = sprintId;
      return this;
    }

    public CreateNewCycleDto build() {
      return new CreateNewCycleDto(this);
    }
  }
}
