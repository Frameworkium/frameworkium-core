package com.frameworkium.core.common.reporting.jira.service


import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.Metadata
import groovy.json.JsonBuilder
import io.restassured.http.ContentType
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

class ProjectSpec extends Specification {
    String projectKey = "KEY-${UUID.randomUUID().toString()}"
    Long projectId = 1001
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

    def "Get project from Jira"() {
        given:
            Project project = new Project()
            def projectBaseUrl = JiraEndpoint.PROJECT.getUrl()
            def projectUrl = "${projectBaseUrl}/${projectKey}"
            String responseJson = createMockedResponse(projectKey, projectId)
            wireMock.register(get(urlPathEqualTo(projectUrl))
                    .withMetadata(Metadata.metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(200)
                            .withBody(responseJson)
                            .withHeader("Content-Type", ContentType.JSON as String))
            )
        when:
            def response = project.getProject(projectKey)
        then:
            with(response) {
                response.id == projectId
                response.key == projectKey
            }
    }

    private static def createMockedResponse(String projectKey, Long projectId) {
        def obj = ["id": "${projectId as String}", "key": projectKey]
        return new JsonBuilder(obj)
    }
}
