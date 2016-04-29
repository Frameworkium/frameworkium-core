package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import com.jayway.restassured.path.json.JsonPath;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;

public class SearchIssues {

    private final static String jiraAPIURI = Property.JIRA_URL.getValue() + Config.jiraRestURI;
    private final JsonPath jsonPath;

    public SearchIssues(final String query) {
        try {
            jsonPath = given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                    .log().everything()
                    .then()
                    .get(jiraAPIURI + String.format("search?jql=%s&maxResults=1000", query))
                    .andReturn().jsonPath();

        } catch (RuntimeException re) {
            throw new RuntimeException("Problem with JIRA or JQL.");
        }
        if (null == jsonPath || null == jsonPath.getList("issues")) {
            throw new RuntimeException(String.format("No JIRA issues returned by specified JQL '%s'", query));
        }
    }

    public List<String> getKeys() {
        return jsonPath.getList("issues.key");
    }

    public List<String> getSummaries() {
        return jsonPath.getList("issues.fields.summary");
    }

    public String getKeyForSummary(final String summary) {
        return jsonPath.getString(String.format("issues.find {it.fields.summary == '%s'}.key", summary));
    }
}
