package com.frameworkium.core.common.reporting.jira.dto.cycle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.frameworkium.core.api.dto.AbstractDTO;

import java.time.LocalDate;

@JsonDeserialize(builder = CycleLightDto.Builder.class)
public class CycleLightDto extends AbstractDTO<CycleLightDto> {
    public String name;
    public String build;
    public String environment;
    public String description;
    public LocalDate startDate;
    public LocalDate endDate;
    public Long projectId;
    public Long versionId;

    private CycleLightDto(final Builder builder) {
        name = builder.name;
        build = builder.build;
        environment = builder.environment;
        description = builder.description;
        startDate = builder.startDate;
        endDate = builder.endDate;
        projectId = builder.projectId;
        versionId = builder.versionId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String name;
        private String build;
        private String environment;
        private String description;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/MMM/yy",
                with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/MMM/yy",
                with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
        private LocalDate endDate;
        private Long projectId;
        private Long versionId;

        private Builder() {}

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder environment(final String environment) {
            this.environment = environment;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder startDate(final LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(final LocalDate endDate) {
            this.endDate = endDate;
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

        public CycleLightDto build() {
            return new CycleLightDto(this);
        }
    }
}
