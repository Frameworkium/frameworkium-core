package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.dto.cycle.CreateNewCycleDto
import com.frameworkium.core.common.reporting.jira.dto.cycle.CycleLightDto
import com.frameworkium.core.common.reporting.jira.dto.cycle.CycleListDto
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint
import com.frameworkium.core.common.reporting.jira.zapi.Cycle
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class CycleSpec extends Specification {
    String cycleBaseUrl = ZephyrEndpoint.CYCLE.getUrl()
    Cycle cycle = new Cycle()
    Long projectId = new Random().nextLong()
    Long versionId = new Random().nextLong()

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

    def "Get list of Zephyr cycles"() {
        given:
            def cycleResponseBody = createMockedCycleListResponse(projectId, versionId)
            wireMock.register(get(urlPathEqualTo(cycleBaseUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .withQueryParam("projectId", equalTo(projectId as String))
                    .withQueryParam("versionId", equalTo(versionId as String))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(cycleResponseBody)
                    )
            )
        when:
            CycleListDto cycleResponse = cycle.getListOfCycle(projectId, versionId)
        then:
            with(cycleResponse) {
                def m = it.getMap()
                it.recordsCount == 1
                m.get("-1").projectKey == "ANON"
                m.get("-1").cycleLightDto.versionId == versionId
                m.get("-1").cycleLightDto.projectId == projectId
                m.get("-1").cycleLightDto.description == "Best cycle eva!"
            }
    }

    def "Create new Zephyr cycle"() {
        given:
            def createCycleRequestBody = createMockedCycleRequestBody(projectId, versionId)
            def createCycleSuccessResponse = createMockedCycleSuccessResponse()
            wireMock.register(post(urlPathEqualTo(cycleBaseUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(createCycleRequestBody))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(createCycleSuccessResponse)
                    )
            )
        when:
            def createNewCycleDto = CreateNewCycleDto.newBuilder()
                    .cycleLightDto(CycleLightDto.newBuilder()
                            .versionId(versionId)
                            .projectId(projectId)
                            .description("Awesome description")
                            .name("name-1")
                            .environment("env-1")
                            .build())
                    .sprintId("SPRINT-1")
                    .cloneCycleId("c1")
                    .build()

            cycle.createNewCycle(createNewCycleDto)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathEqualTo(cycleBaseUrl))
                    .withRequestBody(equalToJson(createCycleRequestBody)))
            loggedRequests.size() == 1
    }

    private static String createMockedCycleListResponse(Long projectId, Long versionId) {
        def obj = ["-1"  : [
                "totalExecutions"                     : 0,
                "endDate"                             : "",
                "description"                         : "Best cycle eva!",
                "totalExecuted"                       : 0,
                "started"                             : "",
                "versionName"                         : "1.13.1",
                "cycleOrderId"                        : null,
                "isExecutionWorkflowEnabledForProject": true,
                "expand"                              : "executionSummaries",
                "projectKey"                          : "ANON",
                "versionId"                           : versionId,
                "environment"                         : "",
                "createdDate"                         : "",
                "totalCycleExecutions"                : 0,
                "isTimeTrackingEnabled"               : true,
                "build"                               : "",
                "ended"                               : "",
                "name"                                : "Ad hoc",
                "modifiedBy"                          : "",
                "projectId"                           : projectId,
                "startDate"                           : ""
        ], "recordsCount": 1]
        return new JsonBuilder(obj).toString()
    }

    private static String createMockedCycleRequestBody(Long projectId, Long versionId) {
        def obj = [
                "cloneCycleId": "c1",
                "sprintId"    : "SPRINT-1",
                name          : "name-1",
                environment   : "env-1",
                description   : "Awesome description",
                projectId     : projectId,
                versionId     : versionId
        ]
        return new JsonBuilder(obj).toString()
    }

    private static String createMockedCycleSuccessResponse() {
        def obj = ["id"             : "adf",
                   "responseMessage": "Success"]
        return new JsonBuilder(obj).toString()
    }
}
