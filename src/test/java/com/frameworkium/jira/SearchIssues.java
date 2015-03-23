package com.frameworkium.jira;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.preemptive;

import java.util.List;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class SearchIssues {

    private final static AuthenticationScheme auth = preemptive().basic(Config.jiraUsername, Config.jiraPassword);
    private final static String jiraURI = JIRA_URL.getValue() + Config.jiraRestURI;

    private final JsonPath jsonPath;

    static {
        RestAssured.baseURI = jiraURI;
        RestAssured.authentication = auth;
    }

    public SearchIssues(final String query) {
        try {
            jsonPath = get(String.format("search?jql=%s&maxResults=1000", query)).andReturn().jsonPath();
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
