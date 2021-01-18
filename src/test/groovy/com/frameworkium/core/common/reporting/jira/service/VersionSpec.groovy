package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.json.JsonBuilder
import io.restassured.http.ContentType
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class VersionSpec extends Specification {
    Long versionId = 4654
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

    def "Get Jira project version"() {
        given:
            Version version = new Version()
            def versionBaseUrl = JiraEndpoint.VERSION.getUrl()
            def versionUrl = "${versionBaseUrl}/${versionId}"
            String responseJson = createMockedResponse(versionId)
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
                response.projectId == 33395
                response.name == "Phase 2B"
                response.description == "An excellent version"
                response.archived == false
            }
    }

    private static String createMockedResponse(Long versionId) {
        def obj = [
                self       : "https://engineering/jira/rest/api/2/version/103486",
                description: "An excellent version",
                id         : "103486",
                name       : "Phase 2B",
                archived   : false,
                released   : false,
                projectId  : 33395
        ]
        return new JsonBuilder(obj)
    }
}
