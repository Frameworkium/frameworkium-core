package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.reporting.jira.dto.execution.AddTestToCycleOperationDto;
import com.frameworkium.core.common.reporting.jira.dto.execution.UpdateExecutionOperationDto;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;
import com.frameworkium.core.common.reporting.jira.service.AbstractJiraService;

import static org.apache.http.HttpStatus.SC_OK;

public class Execution2 extends AbstractJiraService {
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

    public String updateExecutionDetails(UpdateExecutionOperationDto updateExecutionOperationDto, Integer id) {
        return getRequestSpec()
                .basePath(ZephyrEndpoint.EXECUTION.getUrl())
                .pathParam("id", id)
                .body(updateExecutionOperationDto)
                .put("/{id}/execute")
                .then()
                .log().ifValidationFails()
                .statusCode(SC_OK)
                .extract()
                .asString(); // get a success token
    }
}
