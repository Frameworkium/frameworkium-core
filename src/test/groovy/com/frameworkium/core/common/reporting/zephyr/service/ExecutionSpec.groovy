package com.frameworkium.core.common.reporting.zephyr.service


import spock.lang.Specification

class ExecutionSpec extends Specification {
//    def execution = new com.frameworkium.core.common.reporting.jira.zapi.Execution("KEY-1")

    def setupSpec() {
        System.properties["jiraURL"] = "http://localhost:8080"
        System.properties["jiraUsername"] = "username"
        System.properties["jiraPassword"] = "password"
        System.properties["resultVersion"] = "aResultVersion"
    }

    def setup() {
        SearchExecutions searchExecutions = Spy(SearchExecutions, constructorArgs: ["123"])
        searchExecutions.getExecutionIds() >> ["123"]
    }

    def "update zephyr execution"() {
        given:
            def execution = new com.frameworkium.core.common.reporting.jira.zapi.Execution("KEY-1")
//            SearchExecutions searchExecutions = Spy(SearchExecutions, constructorArgs: ['KEY-1'])
        when:
            searchExecutions.getExecutionIds() >> ["122", "456"]

            execution.update(200, "comment", "attachments")
        then:
            1 * execution.updateStatusAndComment
            1 * execution.replaceExistingAttachments
    }

//    private static String createJsonResponse() {
//        def obj = []
//        return new JsonBuilder(obj).toString()
//    }
}
