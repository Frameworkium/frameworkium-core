package com.frameworkium.core.common.reporting.jira.dto.execution;

import com.fasterxml.jackson.databind.annotation.*;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.frameworkium.core.api.dto.AbstractDTO;

import java.util.List;

@JsonDeserialize(builder = AddTestToCycleOperationDto.Builder.class)
public class AddTestToCycleOperationDto extends AbstractDTO<AddTestToCycleOperationDto> {
    public String cycleId;
    public List<String> issues;
    public String searchId;
    public String method;
    @JsonSerialize(using = ToStringSerializer.class)
    public Long projectId;
    @JsonSerialize(using = ToStringSerializer.class)
    public Long versionId;


    private AddTestToCycleOperationDto(final Builder builder) {
        cycleId = builder.cycleId;
        issues = builder.issues;
        searchId = builder.searchId;
        method = builder.method;
        projectId = builder.projectId;
        versionId = builder.versionId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String cycleId;
        private List<String> issues;
        private String searchId;
        private String method;
        private Long projectId;
        private Long versionId;

        private Builder() {}

        public Builder cycleId(final String cycleId) {
            this.cycleId = cycleId;
            return this;
        }

        public Builder issues(final List<String> issues) {
            this.issues = issues;
            return this;
        }

        public Builder searchId(final String searchId) {
            this.searchId = searchId;
            return this;
        }

        public Builder method(final String method) {
            this.method = method;
            return this;
        }

        public Builder projectId(final Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder versionId(final Long versionId) {
            this.versionId = versionId;
            return this;
        }

        public AddTestToCycleOperationDto build() {
            return new AddTestToCycleOperationDto(this);
        }
    }
}
