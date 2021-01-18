package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.cycle.*;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;

import static org.apache.http.HttpStatus.SC_OK;

public class Cycle extends AbstractJiraService {
    public CycleListDto getListOfCycle(Long projectId, Long versionId) {
        return getRequestSpec()
                .basePath(ZephyrEndpoint.CYCLE.getUrl())
                .queryParam("projectId", projectId)
                .queryParam("versionId", versionId)
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract().body()
                .as(CycleListDto.class);
    }

    public CreateCycleSuccessDto createNewCycle(CreateNewCycleDto createNewCycleDto) {
        return getRequestSpec()
                .basePath(ZephyrEndpoint.CYCLE.getUrl())
                .body(createNewCycleDto)
                .post()
                .then()
                .log().ifValidationFails()
                .extract()
                .as(CreateCycleSuccessDto.class);
    }
}
