package com.frameworkium.core.common.reporting.jira.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.frameworkium.core.common.reporting.jira.dto.executionsearch.ExecutionSearchListDto
import com.frameworkium.core.common.reporting.jira.zapi.ExecutionSearch
import groovy.json.JsonBuilder
import spock.lang.Requires
import spock.lang.Specification

@Requires({ System.getProperty("zapiCycleRegEx") != null })
class ExecutionSearchUtilSpec extends Specification {
    def versionId = new Random().nextLong()
    def projectId = new Random().nextLong()
    def zqlQuery = UUID.randomUUID().toString()

    def "Get execution Ids optionally with Property.ZAPI_REGEX_CYCLE set"() {
        given:
            ExecutionSearch executionSearch = Mock()
            executionSearch.search(_ as String) >> createMockedSearchResponse(projectId, versionId)
            ExecutionSearchUtil searchUtil = new ExecutionSearchUtil(executionSearch, zqlQuery)
        when:
            List<Integer> response = searchUtil.getExecutionIdsByZAPICycleRegex()
        then:
            with(response) {
                it.size() == 1
                it == [409]
            }

    }

    def "Get execution status Ids optionally with Property.ZAPI_REGEX_CYCLE set"() {
        given:
            ExecutionSearch executionSearch = Mock()
            executionSearch.search(_ as String) >> createMockedSearchResponse(projectId, versionId)
            ExecutionSearchUtil searchUtil = new ExecutionSearchUtil(executionSearch, zqlQuery)
        when:
            List<Integer> response = searchUtil.getExecutionStatusesByZAPICycleRegex()
        then:
            with(response) {
                it.size() == 1
                it == [-1]
            }
    }

    private static def createMockedSearchResponse(Long projectId, Long versionId) {
        def obj = [
                "executions"      : [
                        [
                                "id"                  : 409,
                                "orderId"             : 1,
                                "cycleId"             : -1,
                                "cycleName"           : "myCycle",
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
                                "comment"             : "",
                                "htmlComment"         : "",
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
                        ],
                        [
                                "id"                  : 410,
                                "orderId"             : 1,
                                "cycleId"             : -1,
                                "cycleName"           : "not my cycle",
                                "issueId"             : "10125",
                                "issueKey"            : "IE-1",
                                "issueSummary"        : "cx",
                                "labels"              : [],
                                "issueDescription"    : "",
                                "projectKey"          : "IE",
                                "projectId"           : projectId,
                                "project"             : "IE",
                                "projectAvatarId"     : 10225,
                                "priority"            : "Major",
                                "components"          : [],
                                "versionId"           : versionId,
                                "versionName"         : "Unscheduled",
                                "status"              : [
                                        "id"         : 1,
                                        "name"       : "EXECUTED",
                                        "description": "The test was executed.",
                                        "color"      : "#A0A0A0",
                                        "type"       : 0
                                ],
                                "executedOn"          : "",
                                "creationDate"        : "27/Apr/16",
                                "comment"             : "",
                                "htmlComment"         : "",
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
                "totalCount"      : 2,
                "executionIds"    : []
        ]
        def jsonString = new JsonBuilder(obj).toString()
        def mapper = new ObjectMapper().findAndRegisterModules()
        return mapper.readValue(jsonString, ExecutionSearchListDto)
    }
}