package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.dto.execution.AddTestToCycleOperationDto;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;

import static org.apache.http.HttpStatus.SC_OK;

public class Execution extends AbstractJiraService {
    public String addTestsToCycle(AddTestToCycleOperationDto addTestToCycleOperationDto) {
        return getRequestSpec()
                .basePath(ZephyrEndpoint.ADD_TEST_TO_EXECUTION.getUrl())
                .body(addTestToCycleOperationDto)
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .asString(); //gets a jobprogresstoken
    }
}
