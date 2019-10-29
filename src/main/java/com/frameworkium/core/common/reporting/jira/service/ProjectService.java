package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.project.ProjectDto;
import com.frameworkium.core.common.reporting.jira.dto.version.VersionDto;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;

public class ProjectService extends AbstractJiraService {
    private static final String PROJECT_REST_PATH = "/rest/api/2/project";

    protected ProjectService() {
    }

    public ProjectDto getProject(String projectIdOrKey) {
        return getRequestSpec()
                .basePath(PROJECT_REST_PATH)
                .pathParam("projectIdOrKey", projectIdOrKey)
                .get("/{projectIdOrKey}")
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .as(ProjectDto.class);
    }

    public List<VersionDto> getProjectVersions(String projectIdOrKey) {
        return getRequestSpec()
                .basePath(PROJECT_REST_PATH)
                .pathParam("projectIdOrKey", projectIdOrKey)
                .get("/{projectIdOrKey}/versions")
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .body().jsonPath()
                .getList("", VersionDto.class);
    }
}
