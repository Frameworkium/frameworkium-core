package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

@JsonDeserialize(builder = ExecutionLightDto.Builder.class)
public class ExecutionLightDto extends AbstractDTO<ExecutionLightDto> {
    public Long id;
    public Long orderId;
    public String comment;
    public String htmlComment;
    public Long cycleId;
    public String cycleName;
    public Long versionId;
    public String versionName;
    public Long projectId;
    public String issueKey;
    public String projectKey;

    public ExecutionLightDto(Builder builder) {
        this.id = builder.id;
        this.orderId = builder.orderId;
        this.comment = builder.comment;
        this.htmlComment = builder.htmlComment;
        this.cycleId = builder.cycleId;
        this.cycleName = builder.cycleName;
        this.versionId = builder.versionId;
        this.versionName = builder.versionName;
        this.projectId = builder.projectId;
        this.issueKey = builder.issueKey;
        this.projectKey = builder.projectKey;
    }

    public static ExecutionLightDto.Builder newBuilder() {
        return new ExecutionLightDto.Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private Long id;
        private Long orderId;
        private String comment;
        private String htmlComment;
        private Long cycleId;
        private String cycleName;
        private Long versionId;
        private String versionName;
        private Long projectId;
        private String issueKey;
        private String projectKey;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder htmlComment(String htmlComment) {
            this.htmlComment = htmlComment;
            return this;
        }

        public Builder cycleId(Long cycleId) {
            this.cycleId = cycleId;
            return this;
        }

        public Builder cycleName(String cycleName) {
            this.cycleName = cycleName;
            return this;
        }

        public Builder versionId(Long versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder versionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder issueKey(String issueKey) {
            this.issueKey = issueKey;
            return this;
        }

        public Builder projectKey(String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public ExecutionLightDto build() {
            return new ExecutionLightDto(this);
        }
    }
}
