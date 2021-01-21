package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.dto.execution.AddTestToCycleOperationDto
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint
import com.frameworkium.core.common.reporting.jira.zapi.Execution
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class Execution2Spec extends Specification {
    Long projectId = new Random().nextLong()
    Long versionId = new Random().nextLong()
    String method = "1"
    String cycleId = new Random().nextLong().toString()
    List<String> issues = ["TEST-1", "TEST-2"]
    String addTestToExecutionBaseUrl = ZephyrEndpoint.ADD_TEST_TO_EXECUTION.getUrl()
    Execution execution = new Execution()
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    def setupSpec() {
        System.properties["jiraURL"] = "http://localhost:8080"
        System.properties["jiraUsername"] = "username"
        System.properties["jiraPassword"] = "password"
    }

    def cleanup() {
        wireMock.removeStubsByMetadataPattern(matchingJsonPath(/$.id/, equalTo(stubId)))
    }

    def "Add tests to Zephyr execution cycle"() {
        given:
            def executionRequestBody = createMockedExecutionRequestBody(projectId, versionId, cycleId, method, issues)
            wireMock.register(post(urlPathEqualTo(addTestToExecutionBaseUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(executionRequestBody))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json"))
            )
        when:
            AddTestToCycleOperationDto addTestToCycleOperationDto = AddTestToCycleOperationDto.newBuilder()
                    .cycleId(cycleId)
                    .projectId(projectId)
                    .versionId(versionId)
                    .issues(issues)
                    .method(method)
                    .build()

            execution.addTestsToCycle(addTestToCycleOperationDto)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(
                    postRequestedFor(urlPathEqualTo(addTestToExecutionBaseUrl))
                            .withRequestBody(equalToJson(executionRequestBody)))
            loggedRequests.size() == 1
    }

    private static def createMockedExecutionRequestBody(Long projectId, Long versionId, String cycleId, String method, List<String> issues) {
        def obj = [
                "cycleId"  : cycleId,
                "method"   : method,
                "projectId": projectId.toString(),
                "versionId": versionId.toString(),
                "issues"   : issues
        ]
        return new JsonBuilder(obj).toString()
    }
}
