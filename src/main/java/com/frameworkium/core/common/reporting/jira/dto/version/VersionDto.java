package com.frameworkium.core.common.reporting.jira.dto.version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.*;
import com.frameworkium.core.api.dto.AbstractDTO;

import java.time.LocalDate;

@JsonDeserialize(builder = VersionDto.Builder.class)
public class VersionDto extends AbstractDTO<VersionDto> {
    public final String self;
    public final Long id;
    public final String description;
    public final String name;
    public final Boolean archived;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public final LocalDate releaseDate;
    public final Boolean released;
    public final Boolean overdue;
    public final LocalDate userReleaseDate;
    public final String project;
    public final Long projectId;

    private VersionDto(final Builder builder) {
        self = builder.self;
        id = builder.id;
        description = builder.description;
        name = builder.name;
        archived = builder.archived;
        released = builder.released;
        releaseDate = builder.releaseDate;
        overdue = builder.overdue;
        userReleaseDate = builder.userReleaseDate;
        project = builder.project;
        projectId = builder.projectId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private String self;
        @JsonSerialize(as = String.class)
        private Long id;
        private String description;
        private String name;
        private Boolean archived;
        private Boolean released;
        private LocalDate releaseDate;
        private Boolean overdue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MMM/yy",
                with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
        private LocalDate userReleaseDate;
        private String project;
        private Long projectId;

        private Builder() {}

        public Builder self(final String self) {
            this.self = self;
            return this;
        }

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder archived(final Boolean archived) {
            this.archived = archived;
            return this;
        }

        public Builder released(final Boolean released) {
            this.released = released;
            return this;
        }

        public Builder releaseDate(final LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder overdue(final Boolean overdue) {
            this.overdue = overdue;
            return this;
        }

        public Builder userReleaseDate(final LocalDate userReleaseDate) {
            this.userReleaseDate = userReleaseDate;
            return this;
        }

        public Builder project(final String project) {
            this.project = project;
            return this;
        }

        public Builder projectId(final Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public VersionDto build() {
            return new VersionDto(this);
        }
    }
}
