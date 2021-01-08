package com.frameworkium.core.common.reporting.jira.service


import spock.lang.Specification

class SearchServiceSpec extends Specification {
    def "Getting keys from JQL JIRA search"() {
        given:
            // todo - parameterize jiraURL
            System.properties["jiraURL"] = "http://localhost:8089/jira/rest/api/2"
            System.properties["jiraUsername"] = "username"
            System.properties["jiraPassword"] = "password"
        when:
            //wiremock stub (jiraSearchServiceSpec.json) - ensure that the query matches in the stub
            def searchService = new SearchService("searchServiceSpec")
        then:
            searchService.getKeys() == ["KEY-1", "KEY-2"]
    }
}
