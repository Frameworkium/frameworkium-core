package com.frameworkium.core.common.reporting.jira.service


import groovy.json.JsonBuilder
import io.restassured.path.json.JsonPath
import spock.lang.Specification

class AttachmentSpec extends Specification {
    def "Get attachment Ids"() {
        given:
            String issueKey = "KEY-${UUID.randomUUID().toString()}"
            JsonPath mockedResponse = createMockedResponse(issueKey)
            Issue mockedIssue = Stub(Issue)
            Attachment attachment = new Attachment(mockedIssue)
        when:
            mockedIssue.getIssue() >> mockedResponse
            List<String> ids = attachment.getIds()
        then:
            with(ids) {
                ids == ["10000", "10001"]
            }
    }

    private static JsonPath createMockedResponse(String issueKey) {
        def obj = ["id"    : "6767",
                   "key"   : issueKey,
                   "fields": [
                           "watcher"   : ["isWatching": false,
                                          "watchCount": 1],
                           "attachment": [[
                                                  "id"       : "10000",
                                                  "filename" : "picture1.jpg",
                                                  "created"  : "2017-12-07T09:23:19.542+0000",
                                                  "size"     : 23123,
                                                  "mimeType" : "image/jpeg",
                                                  "content"  : "http://www.example.com/jira/attachments/10000",
                                                  "thumbnail": "http://www.example.com/jira/secure/thumbnail/10000"
                                          ],
                                          [
                                                  "id"       : "10001",
                                                  "filename" : "picture2.jpg",
                                                  "created"  : "2017-11-11T09:23:19.542+0000",
                                                  "size"     : 45644,
                                                  "mimeType" : "image/jpeg",
                                                  "content"  : "http://www.example.com/jira/attachments/10001",
                                                  "thumbnail": "http://www.example.com/jira/secure/thumbnail/10001"
                                          ]
                           ]
                   ]
        ]
        String body = new JsonBuilder(obj).toString()
        JsonPath jsonPath = JsonPath.from(body)
        return jsonPath
    }
}
