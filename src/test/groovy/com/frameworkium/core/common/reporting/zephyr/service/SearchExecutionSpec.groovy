package com.frameworkium.core.common.reporting.zephyr.service

import com.frameworkium.core.common.reporting.zephyr.endpoint.ZephyrEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.Metadata
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class SearchExecutionSpec extends Specification {

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

    def "Search for Zephyr execution Ids"() {
        given:
            String url = ZephyrEndpoint.SEARCH.getUrl()
            String aZqlQuery = "aZqlQuery"

            String body = createMockedJsonResponse()
            wireMock.register(get(urlPathMatching(url))
                    .withQueryParam("zqlQuery", equalTo(aZqlQuery))
                    .withMetadata(Metadata.metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(200)
                            .withBody(body))
            )
        when:
            def searchExecutions = new SearchExecutions(aZqlQuery)
            def ids = searchExecutions.getExecutionIds()
        then:
            with(ids) {
                it == [409]
            }
    }

    def "Search for Zephyr execution statuses"() {
        given:
            String url = ZephyrEndpoint.SEARCH.getUrl()
            String aZqlQuery = "aZqlQuery"

            String body = createMockedJsonResponse()
            wireMock.register(get(urlPathMatching(url))
                    .withQueryParam("zqlQuery", equalTo(aZqlQuery))
                    .withMetadata(Metadata.metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(200)
                            .withBody(body))
            )
        when:
            def searchExecutions = new SearchExecutions(aZqlQuery)
            def ids = searchExecutions.getExecutionStatuses()
        then:
            with(ids) {
                it == [-1]
            }
    }

    private static String createMockedJsonResponse() {
        def obj = [
                "executions"      : [[
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
                                             "projectId"           : 10201,
                                             "project"             : "IE",
                                             "projectAvatarId"     : 10224,
                                             "priority"            : "Major",
                                             "components"          : [],
                                             "versionId"           : -1,
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
