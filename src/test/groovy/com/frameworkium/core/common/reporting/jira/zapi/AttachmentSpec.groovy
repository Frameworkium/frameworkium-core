package com.frameworkium.core.common.reporting.jira.zapi

import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.json.JsonBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.common.Metadata.metadata

class AttachmentSpec extends Specification {
    def attachment = new Attachment()
    Integer entityId = new Random().nextInt()
    String entityType = "EXECUTION"
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

    def "Get Zephyr attachment by entity"() {
        given:
            String url = ZephyrEndpoint.ATTACHMENT_BY_ENTITY.getUrl();
            String responseBody = createMockedGetAttachmentByEntityResponse();
            wireMock.register(get(urlPathMatching(url))
                    .withMetadata(metadata().attr("id", stubId))
                    .withQueryParam("entityType", equalTo(entityType))
                    .withQueryParam("entityId", equalTo(entityId.toString()))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)
                    )
            )
        when:
            def response = attachment.getAttachmentByEntity(entityId, entityType)
        then:
            with(response) {
                it.data.size() == 2
                it.data.collect({ it.fileId }) == ["20", "22"]
            }
    }

    def "Delete Zephyr attachment"() {
        given:
            String url = ZephyrEndpoint.ATTACHMENT.getUrl() + "/${entityId}";
            wireMock.register(delete(urlEqualTo(url))
                    .willReturn(aResponse().withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"success\": \"Attachment je-3.2.0.32002508-local.obr successfully deleted\"}"))
            )
        when:
            attachment.deleteAttachment(entityId)
        then:
            def loggedRequests = wireMock.find(deleteRequestedFor(urlEqualTo(url)))
            loggedRequests.size() == 1
    }

    def "Add Zephyr attachment"() {
        given:
            String url = ZephyrEndpoint.ATTACHMENT.getUrl()
            wireMock.register(post(urlPathMatching(url))
                    .withQueryParam("entityType", equalTo(entityType))
                    .withQueryParam("entityId", equalTo(entityId.toString()))
                    .willReturn(aResponse().withStatus(200))
            )
        when:
            attachment.addAttachments(entityId, entityType, createNewFile("temp-fasdfa.txt"))
        then:
            def loggedRequests = wireMock.find(postRequestedFor(urlPathMatching(url))
                    .withQueryParam("entityType", equalTo(entityType))
                    .withQueryParam("entityId", equalTo(entityId.toString())))
            loggedRequests.size() == 1
    }

    private static def createMockedGetAttachmentByEntityResponse() {
        def obj = [
                "data": [
                        [
                                "fileName"       : "ABC-60 (1) (1).doc",
                                "dateCreated"    : "Today 5:15 PM",
                                "fileSize"       : "7736",
                                "fileIcon"       : "word.gif",
                                "author"         : "vm_admin",
                                "fileIconAltText": "Microsoft Word",
                                "comment"        : "",
                                "fileId"         : "20"
                        ],
                        [
                                "fileName"       : "ABC-60 (1).doc",
                                "dateCreated"    : "Today 5:18 PM",
                                "fileSize"       : "7736",
                                "fileIcon"       : "word.gif",
                                "author"         : "vm_admin",
                                "fileIconAltText": "Microsoft Word",
                                "comment"        : "",
                                "fileId"         : "22"
                        ]
                ]
        ]
        return new JsonBuilder(obj).toString();
    }

    private static File createNewFile(String filename) {
        File file = new File(filename)
        file.createNewFile()
        return file
    }
}
