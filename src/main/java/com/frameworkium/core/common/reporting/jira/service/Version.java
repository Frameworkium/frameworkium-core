package com.frameworkium.core.common.reporting.jira.service;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

import com.frameworkium.core.common.reporting.jira.dto.version.VersionDto;
import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;

public class Version extends AbstractJiraService {
  public VersionDto getVersion(String versionId) {
    return getRequestSpec()
        .basePath(JiraEndpoint.VERSION.getUrl())
        .pathParam("id", versionId)
        .get("/{id}")
        .then()
        .log().ifValidationFails()
        .statusCode(SC_OK)
        .extract()
        .as(VersionDto.class);
  }

  public VersionDto createVersion(VersionDto versionDto) {
    return getRequestSpec()
        .basePath(JiraEndpoint.VERSION.getUrl())
        .body(versionDto)
        .post()
        .then()
        .log().ifValidationFails()
        .statusCode(SC_CREATED)
        .extract()
        .as(VersionDto.class);
  }
}
