package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.version.VersionDto;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class VersionService extends AbstractJiraService {
    private static final String VERSION_REST_PATH = "/rest/api/2/version";

    protected VersionService() {
    }

    public VersionDto getVersion(String versionId) {
        return getRequestSpec()
                .pathParam("id", versionId)
                .basePath(VERSION_REST_PATH)
                .get("/{id}")
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .as(VersionDto.class);
    }

    public VersionDto createVersion(VersionDto versionDto) {
        return getRequestSpec()
                .basePath(VERSION_REST_PATH)
                .body(versionDto)
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(SC_CREATED)
                .extract()
                .as(VersionDto.class);
    }
}
