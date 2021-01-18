package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class IssueLinkSpec extends Specification {
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    IssueLink issueLink = new IssueLink()
    String type = "Duplicate"
    final String inwardIssue = "KEY-${UUID.randomUUID().toString()}"
    final String outwardIssue = "KEY-${UUID.randomUUID().toString()}"

    def setupSpec() {
        System.properties["jiraURL"] = "http://localhost:8080"
        System.properties["jiraUsername"] = "username"
        System.properties["jiraPassword"] = "password"
    }

    def cleanup() {
        wireMock.removeStubsByMetadataPattern(matchingJsonPath(/$.id/, equalTo(stubId)))
    }

    def "Create link between two JIRA issues"() {
        given:
            String issueLinkUrl = JiraEndpoint.ISSUELINK.getUrl()
            def response = createMockedResponse(outwardIssue, inwardIssue, type)
            wireMock.register(post(urlPathEqualTo(issueLinkUrl))
                    .withMetadata(metadata().attr("id", stubId)) //used for remove stub at cleanup
                    .withRequestBody(equalToJson(response))
                    .willReturn(aResponse().withStatus(201))
            )
        when:
            issueLink.linkIssues(type, inwardIssue, outwardIssue)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(issueLinkUrl))
                    .withRequestBody(equalTo(response)))
            loggedRequests[0].bodyAsString == response
    }

    private static def createMockedResponse(String outwardIssue, String inwardIssue, String type) {
        def obj = [
                "outwardIssue": ["key": outwardIssue],
                "inwardIssue" : ["key": inwardIssue],
                "type"        : ["name": type]
        ]
        return new JsonBuilder(obj).toString()
    }
}