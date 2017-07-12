package com.frameworkium.integration.ai.capture.api.tests.executions;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.ai.capture.api.dto.executions.Execution;
import com.frameworkium.integration.ai.capture.api.service.executions.ExecutionService;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class GetExecutionTest extends BaseTest {

    @Test(groups = {"captureApi"})
    public void get_execution(){
        //create execution
        Execution expectedExecution = Execution.newCreateInstance();
        String id = new ExecutionService()
                .createExecution(expectedExecution)
                .executionID;
        //Get the created execution
        Execution responseExecution = new ExecutionService().getExecution(id);
        //assert you can get specific execution
        Equality.checkExecutionInput(expectedExecution, responseExecution);
        assertThat(responseExecution.lastUpdated).isEqualTo(responseExecution.created);
    }

}
