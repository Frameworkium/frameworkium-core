package com.frameworkium.core.common.reporting.jira.dto.cycle;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonDeserialize(builder = CycleDto.Builder.class)
public class CycleDto extends AbstractDTO<CycleDto> {
    @JsonUnwrapped
    public CycleLightDto cycleLightDto;
    public String createdBy;
    public String createdByDisplay;
    public String createdDate;
    public String ended;
    public String expand;
    public Boolean isExecutionWorkflowEnabledForProject;
    public Boolean isTimeTrackingEnabled;
    public String modifiedBy;
    public String projectKey;
    public String started;
    public Long totalCycleExecutions;
    public Long totalDefects;
    public Long totalExecuted;
    public Long totalExecutions;
    public Long totalFolders;
    public String versionName;
    public Long recordsCount;

    private CycleDto(final Builder builder) {
        cycleLightDto = builder.cycleLightDto;
        createdBy = builder.createdBy;
        createdByDisplay = builder.createdByDisplay;
        createdDate = builder.createdDate;
        ended = builder.ended;
        expand = builder.expand;
        isExecutionWorkflowEnabledForProject = builder.isExecutionWorkflowEnabledForProject;
        isTimeTrackingEnabled = builder.isTimeTrackingEnabled;
        modifiedBy = builder.modifiedBy;
        projectKey = builder.projectKey;
        started = builder.started;
        totalCycleExecutions = builder.totalCycleExecutions;
        totalDefects = builder.totalDefects;
        totalExecuted = builder.totalExecuted;
        totalExecutions = builder.totalExecutions;
        totalFolders = builder.totalFolders;
        versionName = builder.versionName;
        recordsCount = builder.recordsCount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        @JsonUnwrapped
        private CycleLightDto cycleLightDto;
        private String createdBy;
        private String createdByDisplay;
        private String createdDate;
        private String ended;
        private String expand;
        private Boolean isExecutionWorkflowEnabledForProject;
        private Boolean isTimeTrackingEnabled;
        private String modifiedBy;
        private String projectKey;
        private String started;
        private Long totalCycleExecutions;
        private Long totalDefects;
        private Long totalExecuted;
        private Long totalExecutions;
        private Long totalFolders;
        private String versionName;
        private Long recordsCount;

        private Builder() {}

        public Builder cycleLightDto(final CycleLightDto cycleLightDto) {
            this.cycleLightDto = cycleLightDto;
            return this;
        }

        public Builder createdBy(final String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdByDisplay(final String createdByDisplay) {
            this.createdByDisplay = createdByDisplay;
            return this;
        }

        public Builder createdDate(final String createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder ended(final String ended) {
            this.ended = ended;
            return this;
        }

        public Builder expand(final String expand) {
            this.expand = expand;
            return this;
        }

        public Builder isExecutionWorkflowEnabledForProject(final Boolean isExecutionWorkflowEnabledForProject) {
            this.isExecutionWorkflowEnabledForProject = isExecutionWorkflowEnabledForProject;
            return this;
        }

        public Builder isTimeTrackingEnabled(final Boolean isTimeTrackingEnabled) {
            this.isTimeTrackingEnabled = isTimeTrackingEnabled;
            return this;
        }

        public Builder modifiedBy(final String modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder projectKey(final String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public Builder started(final String started) {
            this.started = started;
            return this;
        }

        public Builder totalCycleExecutions(final Long totalCycleExecutions) {
            this.totalCycleExecutions = totalCycleExecutions;
            return this;
        }

        public Builder totalDefects(final Long totalDefects) {
            this.totalDefects = totalDefects;
            return this;
        }

        public Builder totalExecuted(final Long totalExecuted) {
            this.totalExecuted = totalExecuted;
            return this;
        }

        public Builder totalExecutions(final Long totalExecutions) {
            this.totalExecutions = totalExecutions;
            return this;
        }

        public Builder totalFolders(final Long totalFolders) {
            this.totalFolders = totalFolders;
            return this;
        }

        public Builder versionName(final String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder recordsCount(final Long recordsCount) {
            this.recordsCount = recordsCount;
            return this;
        }

        public CycleDto build() {
            return new CycleDto(this);
        }
    }
}
