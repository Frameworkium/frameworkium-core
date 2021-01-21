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
    String issueBaseUrl = JiraEndpoint.ISSUE.getUrl()
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
            //GET /rest/api/2/issue/{issueIdOrKey}
            String url = "${issueBaseUrl}/${issueKey}"
            def responseBody = createMockedIssueResponse(issueKey)
            wireMock.register(get(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withBody(responseBody)
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
            String url = "${issueBaseUrl}/${issueKey}"
            def fieldObjects = createMockedFieldResponse(fieldId, fieldName)
            def editFieldRequestBody = createMockedEditFieldRequestBody(fieldId, fieldNewValue)

            wireMock.register(get(urlPathEqualTo(JiraEndpoint.FIELD.getUrl()))
                    .withMetadata(metadata().attr("id", stubId))
                    .willReturn(aResponse().withBody(fieldObjects)
                            .withStatus(200))
            )
            wireMock.register(put(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(editFieldRequestBody))
                    .willReturn(aResponse().withStatus(204))
            )
        when:
            issue.editField(fieldName, fieldNewValue)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(putRequestedFor(urlPathMatching(url))
                    .withRequestBody(equalToJson(editFieldRequestBody)))
            loggedRequests.size() == 1
    }

    def "Add a comment into JIRA issue"() {
        given:
            //POST /rest/api/2/issue/{issueIdOrKey}/comment
            String url = "${issueBaseUrl}/${issueKey}/comment"
            def commentRequestBody = createMockedAddCommentRequestBody("Lorem ipsum")

            wireMock.register(post(urlPathEqualTo(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(commentRequestBody))
                    .willReturn(aResponse().withStatus(201))
            )
        when:
            issue.addComment("Lorem ipsum")
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url))
                    .withRequestBody(equalToJson(commentRequestBody)))
            loggedRequests.size() == 1
    }

    def "Perform a transition on an issue "() {
        given:
            //POST /rest/api/2/issue/{issueIdOrKey}/transitions
            def url = "${issueBaseUrl}/${issueKey}/transitions"
            String transitionName = UUID.randomUUID().toString()
            String transitionId = new Random().nextLong().toString()

            def transitionIdObj = createMockedTransitionIdResponse(transitionId, transitionName)
            wireMock.register(get(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withQueryParam("expand", equalTo("transitions.fields"))
                    .willReturn(aResponse().withBody(transitionIdObj)
                            .withStatus(200))
            )

            def transitionRequestBody = createMockedPerformTransitionRequestBody(transitionId)
            wireMock.register(post(urlPathEqualTo(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withRequestBody(equalToJson(transitionRequestBody))
                    .willReturn(aResponse().withStatus(204))
            )
        when:
            issue.transition(transitionName)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url))
                    .withRequestBody(equalToJson(transitionRequestBody)))
            loggedRequests.size() == 1
    }

    def "Attach a file to a issue"() {
        given:
            //POST /rest/api/2/issue/{issueIdOrKey}/attachments
            def url = "${issueBaseUrl}/${issueKey}/attachments"
            File file = createNewFile("temp.txt")
            wireMock.register(post(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withHeader("X-Atlassian-Token", equalTo("nocheck"))
                    .willReturn(aResponse().withStatus(200))
            )
        when:
            issue.addAttachment(file)
        then:
            List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url)))
            loggedRequests.size() == 1
    }

    private static def createMockedIssueResponse(String issueKey) {
        def obj = ["id"    : "6767",
                   "key"   : issueKey,
                   "fields": [
                           "watcher"   : ["isWatching": false,
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
        return new JsonBuilder(obj).toString()
    }

    private static def createMockedFieldResponse(String fieldId, String fieldName) {
        // used when getting field Id
        def list = [[id: fieldId, name: fieldName]]
        return new JsonBuilder(list).toString()
    }

    private static def createMockedEditFieldRequestBody(String fieldId, String fieldNewValue) {
        // used when editing jira issue
        def obj = [
                "update": [
                        (fieldId): [
                                ["set": fieldNewValue]]
                        ]
                ]
        return new JsonBuilder(obj).toString()
    }

    private static def createMockedAddCommentRequestBody(String comment) {
        def obj = ["body": comment]
        return new JsonBuilder(obj).toString()
    }

    private static def createMockedTransitionIdResponse(String transitionId, String transitionName) {
        // we will get this when we GET transition by Id
        def idObj = [
                "transitions": [["id": transitionId, "name": transitionName]]
        ]
        return new JsonBuilder(idObj).toString()
    }

    private static def createMockedPerformTransitionRequestBody(String transitionId) {
        // use this to perform transition
        def o = ["transition": ["id": transitionId]]
        return new JsonBuilder(o).toString()
    }

    private static File createNewFile(String filename) {
        File file = new File(filename)
        file.createNewFile()
        return file
    }
}
