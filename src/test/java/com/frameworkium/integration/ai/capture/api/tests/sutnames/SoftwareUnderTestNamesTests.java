package com.frameworkium.integration.ai.capture.api.tests.sutnames;

import com.frameworkium.core.api.tests.BaseTest;
import com.frameworkium.integration.ai.capture.api.dto.executions.Execution;
import com.frameworkium.integration.ai.capture.api.dto.sut.Names;
import com.frameworkium.integration.ai.capture.api.service.executions.ExecutionService;
import com.frameworkium.integration.ai.capture.api.service.sut.GetSutNamesService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static java.util.UUID.randomUUID;

public class SoftwareUnderTestNamesTests extends BaseTest {

    private String expectedSutName;

    /**
     * Use @BeforeClass to send some names to Capture before executing the tests
     */
    @BeforeClass(groups = {"captureApi"})
    public void setUp(){
        // create execution
        final Execution createMessage = Execution.newCreateInstance();
        expectedSutName = "sutName " + randomUUID().toString();
        createMessage.softwareUnderTest.name = expectedSutName;
        String id = new ExecutionService()
                .createExecution(createMessage)
                .executionID;
    }


    //This is testing what was sent to capture in setUp()
    @Test(groups = {"captureApi"})
    public void check_for_specific_name(){
        List<String> responseNames = new GetSutNamesService().getNames().names;
        assertThat(responseNames).contains(expectedSutName);
    }

    @Test(groups = {"captureApi"})
    public void get_names(){
        //get expected Names object.
        List<String> expectedNames =  Arrays.asList("Third Test", "frameworkium-core", "My Test App",
                 "Frameworkium", "Fwium-core", "frameworkium",
                 "tfl", "frameworkium-bdd");
        // get Names and store as response object.
        Names responseNames = new GetSutNamesService().getNames();
        //assert success
        assertThat(expectedNames).containsAllIn(responseNames.names);
    }

}
