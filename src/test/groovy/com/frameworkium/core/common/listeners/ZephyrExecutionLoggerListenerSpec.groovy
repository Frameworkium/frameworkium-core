package com.frameworkium.core.common.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.frameworkium.core.common.reporting.jira.dto.executionsearch.ExecutionSearchListDto
import com.frameworkium.core.common.reporting.jira.zapi.*
import groovy.json.JsonBuilder
import io.qameta.allure.TmsLink
import org.testng.ITestResult
import org.testng.internal.ConstructorOrMethod
import spock.lang.Specification

import java.lang.reflect.Method

//@Requires({ System.getProperty("zapiCycleRegEx") != null })
//@UseModules(JiraMockModule)
class ZephyrExecutionLoggerListenerSpec extends Specification {
//    @Inject
//    private ExecutionExtraFactory executionExtraFactory;
//    @Inject
//    private ExecutionSearch executionSearch;


    def setupSpec() {
        System.properties["jiraURL"] = "http://localhost:8080"
        System.properties["jiraUsername"] = "username"
        System.properties["jiraPassword"] = "password"
    }

    def "ZephyrListener logs comment and transitions status to WIP on start"() {
        given:
            ITestResult iTestResult = DeepMock() // fixme this is the issue, it has chain wreck, but not in my control
            Method method = TestIdData.getMethod("tmsLink")
            def corm = Mock(ConstructorOrMethod, constructorArgs: [method])
            iTestResult.method.constructorOrMethod >> corm
            iTestResult.method.constructorOrMethod.getMethod() >> method
            iTestResult.getTestClass().getName() >> TestIdData.getName()
            iTestResult.getMethod().getMethodName() >> method.getName()

            def executionSearch = Mock(ExecutionSearch)
            executionSearch.search(_ as String) >> createMockExecutionSearchListDto()

            ExecutionExtraFactory executionExtraFactory = GroovyMock(ExecutionExtraFactory, constructorArgs: [()-> Mock(Attachment), ()-> Mock(Execution)])
//            ExecutionExtraFactory executionExtraFactory = new ExecutionExtraFactory(() -> Mock(Attachment), () -> Mock(Execution))
            ExecutionExtra executionExtra = executionExtraFactory.create(123) //todo this create is not mocked over? this is because it is a "new"
            executionExtraFactory.create(_ as Integer) >> executionExtra

            def listener = new ZephyrExecutionLoggerListener(executionExtraFactory, executionSearch)
        when:
            listener.onTestStart(iTestResult)
        then:
            1 * executionExtraFactory.create
            1 * executionExtra.updateComment
            1 * executionExtra.updateStatus

    }

    class TestIdData {
        @TmsLink("TMSLink")
        void tmsLink() {}
    }

    private ExecutionSearchListDto createMockExecutionSearchListDto() {
        def obj = [
                "executions": [["id"       : 133,
                                "cycleName": "myCycle"]]
        ]
        def jsonString = new JsonBuilder(obj).toString();
        def mapper = new ObjectMapper().findAndRegisterModules()
        return mapper.readValue(jsonString, ExecutionSearchListDto)
    }
}
