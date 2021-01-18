package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.json.JsonBuilder
import io.restassured.http.ContentType
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class ProjectSpec extends Specification {
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    def projectBaseUrl = JiraEndpoint.PROJECT.getUrl()
    String projectKey = "KEY-${UUID.randomUUID().toString()}"
    Long projectId = new Random().nextLong()
    Project project = new Project()

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
            def projectUrl = "${projectBaseUrl}/${projectKey}"
            String responseJson = createMockedProjectResponse(projectKey, projectId)
            wireMock.register(get(urlPathEqualTo(projectUrl))
                    .withMetadata(metadata().attr("id", stubId))
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

    def "get project versions from Jira"() {
        given:
            def projectVersionUrl = "${projectBaseUrl}/${projectId}/versions"
            def versionsResponse = createMockedListOfVersionsResponse(projectId)
            wireMock.register(get(urlPathEqualTo(projectVersionUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(200)
                            .withBody(versionsResponse))
            )
        when:
            def response = project.getProjectVersions(projectKey)
        then:
            with(response) {
                response.size() == 2
                response.each { it.projectId == projectId }
                response.collect { it.id as String } == ["10000", "10001"]
            }
    }

    private static def createMockedProjectResponse(String projectKey, Long projectId) {
        def obj = ["id": "${projectId as String}", "key": projectKey]
        return new JsonBuilder(obj)
    }

    private static def createMockedListOfVersionsResponse(Long projectId) {
        def obj = [
                [
                        "self"           : "http://www.example.com/jira/rest/api/2/version/10000",
                        "id"             : "10000",
                        "description"    : "An excellent version",
                        "name"           : "New Version 1",
                        "archived"       : false,
                        "released"       : true,
                        "releaseDate"    : "2010-07-06",
                        "overdue"        : true,
                        "userReleaseDate": "6/Jul/2010",
                        "projectId"      : projectId
                ],
                [
                        "self"       : "http://www.example.com/jira/rest/api/2/version/10010",
                        "id"         : "10010",
                        "description": "Minor Bugfix version",
                        "name"       : "Next Version",
                        "archived"   : false,
                        "released"   : false,
                        "overdue"    : false,
                        "projectId"  : projectId
                ]
        ]
        return new JsonBuilder(obj).toString()
    }
}
