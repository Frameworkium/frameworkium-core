package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class ExecutionSearchSpec extends Specification {
    def searchBaseUrl = ZephyrEndpoint.EXECUTE_SEARCH.getUrl()
    def search = new ExecutionSearch()
    def versionId = new Random().nextLong()
    def projectId = new Random().nextLong()
    def zqlQuery = UUID.randomUUID().toString()
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

    def "Search Zephyr for execution"() {
        given:
            def searchResponse = createMockedSearchResponse(projectId, versionId)
            wireMock.register(get(urlPathEqualTo(searchBaseUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .withQueryParam("zqlQuery", equalTo(zqlQuery))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(searchResponse))
            )
        when:
            def searchResults = search.search(zqlQuery)
        then:
            with(searchResults) {
                it.executions[0].versionId == versionId
                it.executions[0].projectId == projectId
                it.executions[0].issueId == "10125"
                it.executions[0].status.id == -1
                it.maxResultAllowed == 20
                it.totalCount == 1
            }
    }

    private static def createMockedSearchResponse(Long projectId, Long versionId) {
        def obj = [
                "executions"      : [
                        [
                                "id"                  : 409,
                                "orderId"             : 1,
                                "cycleId"             : -1,
                                "cycleName"           : "Ad hoc",
                                "issueId"             : "10125",
                                "issueKey"            : "IE-1",
                                "issueSummary"        : "cx",
                                "labels"              : [],
                                "issueDescription"    : "",
                                "projectKey"          : "IE",
                                "projectId"           : projectId,
                                "project"             : "IE",
                                "projectAvatarId"     : 10224,
                                "priority"            : "Major",
                                "components"          : [],
                                "versionId"           : versionId,
                                "versionName"         : "Unscheduled",
                                "status"              : [
                                        "id"         : -1,
                                        "name"       : "UNEXECUTED",
                                        "description": "The test has not yet been executed.",
                                        "color"      : "#A0A0A0",
                                        "type"       : 0
                                ],
                                "executedOn"          : "",
                                "creationDate"        : "27/Apr/16",
                                "comment"             : "",
                                "htmlComment"         : "",
                                "executedBy"          : "",
                                "executedByUserName"  : "",
                                "executionDefects"    : [],
                                "stepDefects"         : [],
                                "executionDefectCount": 0,
                                "stepDefectCount"     : 0,
                                "totalDefectCount"    : 0,
                                "executedByDisplay"   : "",
                                "assignee"            : "",
                                "assigneeUserName"    : "",
                                "assigneeDisplay"     : ""
                        ]
                ],
                "currentIndex"    : 1,
                "maxResultAllowed": 20,
                "linksNew"        : [
                        1
                ],
                "totalCount"      : 1,
                "executionIds"    : []
        ]
        return new JsonBuilder(obj).toString()
    }
}
