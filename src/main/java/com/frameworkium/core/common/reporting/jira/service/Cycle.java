package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.cycle.*;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.REST_ZAPI_PATH;
import static org.apache.http.HttpStatus.SC_OK;

public class Cycle extends AbstractJiraService {
    private static final String CYCLE_REST_PATH = REST_ZAPI_PATH + "cycle";

    public CycleListDto getListOfCycle(Long projectId, Long versionId) {
        return getRequestSpec()
                .basePath(CYCLE_REST_PATH)
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
                .basePath(CYCLE_REST_PATH)
                .body(createNewCycleDto)
                .post()
                .then()
                .log().ifValidationFails()
                .extract()
                .as(CreateCycleSuccessDto.class);
    }
}
