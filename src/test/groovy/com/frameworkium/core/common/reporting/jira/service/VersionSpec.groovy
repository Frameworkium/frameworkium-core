package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.dto.version.VersionDto
import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import io.restassured.http.ContentType
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class VersionSpec extends Specification {
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    Version version = new Version()
    Long versionId = 4654
    Long projectId = new Random().nextLong()
    def versionBaseUrl = JiraEndpoint.VERSION.getUrl()

    def setupSpec() {
        System.properties["jiraURL"] = "http://localhost:8080"
        System.properties["jiraUsername"] = "username"
        System.properties["jiraPassword"] = "password"
    }

    def cleanup() {
        wireMock.removeStubsByMetadataPattern(matchingJsonPath(/$.id/, equalTo(stubId)))
    }

    def "Get Jira project version"() {
        given:
            def versionUrl = "${versionBaseUrl}/${versionId}"
            String responseJson = createMockedResponse(projectId)
            wireMock.register(get(urlPathEqualTo(versionUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(200)
                            .withBody(responseJson)
                            .withHeader("Content-Type", ContentType.JSON as String))
            )
        when:
            def response = version.getVersion(versionId as String)
        then:
            with(response) {
                response.projectId == projectId
                response.name == "Phase 2B"
                response.description == "An excellent version"
                response.archived == false
            }
    }

    def "Create Jira project version"() {
        given:
            String requestBody = createMockedRequestBody(projectId)
            wireMock.register(post(urlPathEqualTo(versionBaseUrl))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(requestBody))
                    .willReturn(aResponse().withStatus(201)
                            .withBody(requestBody)
                            .withHeader("Content-Type", "application/json")
                    ))
        when:
            def versionDto = VersionDto.newBuilder()
                    .description("An excellent version")
                    .name("New Version 1")
                    .archived(false)
                    .released(true)
                    .releaseDate(LocalDate.of(2010, 07, 06))
                    .project("PXA")
                    .projectId(projectId)
                    .build()
            version.createVersion(versionDto)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathEqualTo(versionBaseUrl))
                    .withRequestBody(equalToJson(requestBody)))
            loggedRequests.size() == 1
    }

    private static String createMockedResponse(Long projectId) {
        def obj = [
                self       : "https://engineering/jira/rest/api/2/version/103486",
                description: "An excellent version",
                id         : "103486",
                name       : "Phase 2B",
                archived   : false,
                released   : false,
                projectId  : projectId
        ]
        return new JsonBuilder(obj)
    }

    private static String createMockedRequestBody(Long projectId) {
        def obj = [
                "description": "An excellent version",
                "name"       : "New Version 1",
                "archived"   : false,
                "released"   : true,
                "releaseDate": "2010-07-06",
                "project"    : "PXA",
                "projectId"  : projectId
        ]
        return new JsonBuilder(obj)
    }
}
