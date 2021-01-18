package com.frameworkium.core.common.reporting.jira.service

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.json.JsonBuilder
import io.restassured.path.json.JsonPath
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class IssueSpec extends Specification {
    String issueKey = "KEY-${UUID.randomUUID().toString()}"
    Issue issue = new Issue(issueKey)
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

    def "Get an Issue"() {
        given:
            String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
            //GET /rest/api/2/issue/{issueIdOrKey}
            String url = "${issueBaseUrl}/${issueKey}"

            def obj = ["id"    : "6767",
                       "key"   : issueKey,
                       "fields": [
                               "watcher"    : ["isWatching": false,
                                               "watchCount": 1],
                               "attachment": [[
                                                       "filename" : "picture.jpg",
                                                       "created"  : "2017-12-07T09:23:19.542+0000",
                                                       "size"     : 23123,
                                                       "mimeType" : "image/jpeg",
                                                       "content"  : "http://www.example.com/jira/attachments/10000",
                                                       "thumbnail": "http://www.example.com/jira/secure/thumbnail/10000"
                                               ]]
                       ]
            ]
            String body = new JsonBuilder(obj).toString()
            wireMock.register(get(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withBody(body)
                            .withStatus(200))
            )
        when:
            JsonPath response = issue.getIssue()
        then:
            with(response) {
                response.get("id") == "6767"
                response.get("key") == this.issueKey
                response.get("fields.attachment.find { it.filename == 'picture.jpg'}.size") == 23123
            }
    }

    def "Edit a field in JIRA issue"() {
        given:
            String fieldName = "fieldName"
            String fieldId = "fieldId"
            String fieldNewValue = "fieldNewValue"
            //PUT /rest/api/2/issue/{issueIdOrKey}
            String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
            String url = "${issueBaseUrl}/${issueKey}"

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
            wireMock.register(put(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(204))
            )
        when:
            issue.editField(fieldName, fieldNewValue)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(putRequestedFor(urlPathMatching(url)))
            loggedRequests.last().bodyAsString == expectedBody
    }

    def "Add a comment into JIRA issue"() {
        given:
            String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
            //POST /rest/api/2/issue/{issueIdOrKey}/comment
            String url = "${issueBaseUrl}/${issueKey}/comment"
            String comment = "Lorem ipsum"
            def obj = ["body": comment]
            String expectedBody = new JsonBuilder(obj).toString()

            wireMock.register(post(urlPathEqualTo(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withStatus(201))
            )
        when:
            issue.addComment(comment)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url)))
            loggedRequests.last().bodyAsString == expectedBody
    }

    def "Perform a transition on an issue "() {
        given:
            //POST /rest/api/2/issue/{issueIdOrKey}/transitions
            String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
            def url = "${issueBaseUrl}/${issueKey}/transitions"
            String transitionName = "MyTransition"
            String transitionId = "1212"

            setStubForGettingTransitionId(wireMock, url, transitionId, transitionName, stubId)
            def expectedBody = setStubForPerformingTransition(wireMock, url, transitionId, stubId)

        when:
            issue.transition(transitionName)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url)))
            loggedRequests.last().bodyAsString == expectedBody
    }

    def "Attach a file to a issue"() {
        given:
            //POST /rest/api/2/issue/{issueIdOrKey}/attachments
            String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
            def url = "${issueBaseUrl}/${issueKey}/attachments"
            File file = createNewFile("temp.txt")
            wireMock.register(post(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withHeader("X-Atlassian-Token", equalTo("no-check"))
                    .willReturn(aResponse().withStatus(200))
            )
        when:
            issue.addAttachment(file)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url)))
            loggedRequests.size() == 1
    }

    static def setStubForGettingTransitionId(
            WireMock wireMock, String transitionUrl, String transitionId, String transitionName, String stubId) {
        // we will get this when we GET transition by Id
        def idObj = [
                "transitions": [["id": transitionId, "name": transitionName]]
        ]
        String transitionIdObj = new JsonBuilder(idObj).toString()
        wireMock.register(get(urlPathMatching("${transitionUrl}"))
                .withMetadata(metadata().attr("id", stubId))
                .withQueryParam("expand", equalTo("transitions.fields"))
                .willReturn(aResponse().withBody(transitionIdObj)
                        .withStatus(200))
        )
    }

    static String setStubForPerformingTransition(
            WireMock wireMock, String transitionUrl, String transitionId, String stubId) {
        // use this to perform transition
        def o = [
                "transition": ["id": transitionId]
        ]
        String body = new JsonBuilder(o).toString()
        wireMock.register(post(urlPathEqualTo(transitionUrl))
                .withMetadata(metadata().attr("id", stubId))
                .withRequestBody(equalToJson(body))
                .willReturn(aResponse().withStatus(204))
        )
        return body
    }

    static File createNewFile(String filename) {
        File file = new File(filename)
        file.createNewFile()
        return file
    }
}
