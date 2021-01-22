package com.frameworkium.core.common.reporting.jira.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.frameworkium.core.common.reporting.jira.dto.attachment.AttachmentListDto
import com.frameworkium.core.common.reporting.jira.dto.executionsearch.ExecutionSearchListDto
import com.frameworkium.core.common.reporting.jira.zapi.Attachment
import com.frameworkium.core.common.reporting.jira.zapi.Execution
import com.frameworkium.core.common.reporting.jira.zapi.ExecutionSearch
import groovy.json.JsonBuilder
import spock.lang.Requires
import spock.lang.Specification

@Requires({ System.getProperty("zapiCycleRegEx") != null })
class ExecutionUtilSpec extends Specification {
    def "Update execution details with comments and attachments"() {
        given:
            Execution execution = Mock()
            Attachment attachment = Mock()
            ExecutionSearch executionSearch = Mock()

            attachment.getAttachmentByEntity(_ as Integer, _ as String) >> createMockedGetAttachmentByEntityResponse() //returns 2 attachments
            executionSearch.search(_ as String) >> createMockedSearchResponse(1342342, 898)

            ExecutionSearchUtil executionSearchUtil = Mock(ExecutionSearchUtil, constructorArgs: [executionSearch, "fakeQuery"]) as ExecutionSearchUtil

            executionSearchUtil.executionIdsByZAPICycleRegex >> [12312, 123, 244]
            executionSearchUtil.executionStatusesByZAPICycleRegex >> [1231, 56676]
            ExecutionUtil executionUtil = new ExecutionUtil(executionSearchUtil, execution, attachment, "fakeIssue", "fakeVersion")
        when:
            executionUtil.update(1, "whatever comment", "myAttachment") // to add 1 attachment
        then:
            3 * execution.updateExecutionDetails(_, _) //updates once per id returned by executionIdsByZAPICycleRegex
            6 * attachment.deleteAttachment(_) // number per attachment(per issue) * number of issue returned  by executionIdsByZAPICycleRegex
            3 * attachment.addAttachments(_, _, _) // number of attachment to attach * number of issue returned by executionIdsByZAPICycleRegex
    }

    private static def createMockedSearchResponse(Long projectId, Long versionId) {
        def obj = [
                "executions"      : [
                        [
                                "id"                  : 409,
                                "orderId"             : 1,
                                "cycleId"             : -1,
                                "cycleName"           : "Ad hoc",
                                "issueId"             : "10125",
                                "issueKey"            : "IE-1",
                                "issueSummary"        : "cx",
                                "labels"              : [],
                                "issueDescription"    : "",
                                "projectKey"          : "IE",
                                "projectId"           : projectId,
                                "project"             : "IE",
                                "projectAvatarId"     : 10224,
                                "priority"            : "Major",
                                "components"          : [],
                                "versionId"           : versionId,
                                "versionName"         : "Unscheduled",
                                "status"              : [
                                        "id"         : -1,
                                        "name"       : "UNEXECUTED",
                                        "description": "The test has not yet been executed.",
                                        "color"      : "#A0A0A0",
                                        "type"       : 0
                                ],
                                "executedOn"          : "",
                                "creationDate"        : "27/Apr/16",
                                "comment"             : "hello",
                                "htmlComment"         : "world",
                                "executedBy"          : "",
                                "executedByUserName"  : "",
                                "executionDefects"    : [],
                                "stepDefects"         : [],
                                "executionDefectCount": 0,
                                "stepDefectCount"     : 0,
                                "totalDefectCount"    : 0,
                                "executedByDisplay"   : "",
                                "assignee"            : "",
                                "assigneeUserName"    : "",
                                "assigneeDisplay"     : ""
                        ]
                ],
                "currentIndex"    : 1,
                "maxResultAllowed": 20,
                "linksNew"        : [
                        1
                ],
                "totalCount"      : 1,
                "executionIds"    : []
        ]
        String jsonString = new JsonBuilder(obj).toString()
        def mapper = new ObjectMapper().findAndRegisterModules()
        return mapper.readValue(jsonString, ExecutionSearchListDto.class)
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
        def jsonString = new JsonBuilder(obj).toString();
        def mapper = new ObjectMapper().findAndRegisterModules()
        return mapper.readValue(jsonString, AttachmentListDto)
    }
}
