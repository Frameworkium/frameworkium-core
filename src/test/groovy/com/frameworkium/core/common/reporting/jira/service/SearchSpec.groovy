package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class SearchSpec extends Specification {
    String stubId = UUID.randomUUID().toString() // to uniquely identify stub for cleanup later
    @Shared
    WireMock wireMock = new WireMock("localhost", 8080)

    def cleanup() {
        wireMock.removeStubsByMetadataPattern(matchingJsonPath(/$.id/, equalTo(stubId)))
    }

    def "Getting keys from JQL JIRA search"() {
        given:
            // todo - parameterize jiraURL
            System.properties["jiraURL"] = "http://localhost:8080"
            System.properties["jiraUsername"] = "username"
            System.properties["jiraPassword"] = "password"
            String searchTerm = "ISearchForThis"
            def obj = [
                    "startAt"   : 0,
                    "maxResults": 1000,
                    "total"     : 2,
                    "issues"    : [
                            ["id" : "10001",
                             "key": "KEY-1"
                            ],
                            ["id" : "10002",
                             "key": "KEY-2"
                            ]
                    ]
            ]
            String searchResult = new JsonBuilder(obj).toString()
            wireMock.register(get(urlPathEqualTo(JiraEndpoint.SEARCH.getUrl()))
                    .withMetadata(metadata().attr("id", stubId))
                    .withQueryParam("jql", equalTo(searchTerm))
                    .withQueryParam("startAt", equalTo("0"))
                    .withQueryParam("maxResults", equalTo("1000"))
                    .willReturn(aResponse().withBody(searchResult)
                            .withStatus(200))
            )
        when:
            def searchService = new Search(searchTerm)
        then:
            searchService.getKeys() == ["KEY-1", "KEY-2"]
    }
}
