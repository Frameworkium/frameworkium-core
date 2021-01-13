package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class IssueSpec extends Specification {
    Issue issue = new Issue()
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    def cleanup() {
        wireMock.removeStubsByMetadataPattern(matchingJsonPath(/$.id/, equalTo(stubId)))
    }

    def "editing JIRA issue field value"() {
        given:
            System.properties["jiraURL"] = "http://localhost:8080"
            System.properties["jiraUsername"] = "username"
            System.properties["jiraPassword"] = "password"
            String fieldName = "fieldName"
            String fieldId = "fieldId"
            String fieldNewValue = "fieldNewValue"
            String issueKey = "KEY-${UUID.randomUUID().toString()}"
            String issueUrl = JiraEndpoint.ISSUE.getUrl() + "/${issueKey}"

            // used when getting field Id
            def list = [[id: fieldId, name: fieldName]]
            String fieldObjects = new JsonBuilder(list).toString()
            // used when editing jira issue
            def obj = [
                    "update": [
                            (fieldId): [
                                    ["set": ["value": fieldNewValue]]
                            ]
                    ]
            ]
            String expectedBody = new JsonBuilder(obj).toString()

            wireMock.register(get(urlPathEqualTo(JiraEndpoint.FIELD.getUrl()))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withBody(fieldObjects)
                            .withStatus(200))
            )
            wireMock.register(put(urlPathMatching(issueUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(204))
            )
        when:
            issue.editField(issueKey, fieldName, fieldNewValue)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(putRequestedFor(urlPathMatching(issueUrl)))
            loggedRequests[0].bodyAsString == expectedBody
    }

    def "adding comment into JIRA issue"() {
        given:
            System.properties["jiraURL"] = "http://localhost:8080"
            System.properties["jiraUsername"] = "username"
            System.properties["jiraPassword"] = "password"
            def issueUrl = JiraEndpoint.ISSUE.getUrl()
            String issueKey = "KEY-${UUID.randomUUID().toString()}"
            String commentUrl = "${issueUrl}/${issueKey}/comment"
            String comment = "Lorem ipsum"
            def obj = ["body": comment]
            String expectedBody = new JsonBuilder(obj).toString()

            wireMock.register(post(urlPathEqualTo(commentUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(201))
            )
        when:
            issue.addComment(issueKey, comment)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(commentUrl)))
            loggedRequests[0].bodyAsString == expectedBody
    }
}
