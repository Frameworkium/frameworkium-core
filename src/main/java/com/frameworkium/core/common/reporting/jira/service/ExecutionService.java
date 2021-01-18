package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.execution.AddTestToCycleOperationDto;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.REST_ZAPI_PATH;
import static org.apache.http.HttpStatus.SC_OK;

public class ExecutionService extends AbstractJiraService {
    private static final String EXECUTION_REST_PATH = REST_ZAPI_PATH + "execution";

    public String addTestsToCycle(AddTestToCycleOperationDto addTestToCycleOperationDto) {
        return getRequestSpec()
                .basePath(EXECUTION_REST_PATH)
                .body(addTestToCycleOperationDto)
                .post("/addTestsToCycle")
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .asString(); //gets a jobprogresstoken
    }
}
