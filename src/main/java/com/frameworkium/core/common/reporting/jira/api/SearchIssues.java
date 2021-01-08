package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.reporting.jira.JiraConfig;
import io.restassured.path.json.JsonPath;

import java.util.List;

@Deprecated
public class SearchIssues {

    private static final int MAX_SEARCH_RESULTS = 1000;
    private final JsonPath jsonPath;

    /**
     * Search all issues that match a string.
     */
    public SearchIssues(String query) {
        try {
            jsonPath = JiraConfig.getJIRARequestSpec()
                    .param("jql", query)
                    .param("maxResults", MAX_SEARCH_RESULTS)
                    .when()
                    .get(JiraConfig.JIRA_REST_PATH + "search")
                    .thenReturn().jsonPath();
        } catch (Exception e) {
            throw new IllegalArgumentException("Problem with JIRA or JQL.", e);
        }
        if (jsonPath == null || jsonPath.getList("issues") == null) {
            throw new IllegalStateException(
                    String.format("No JIRA issues returned by specified JQL '%s'", query));
        }
    }

    public List<String> getKeys() {
        return jsonPath.getList("issues.key");
    }

    public List<String> getSummaries() {
        return jsonPath.getList("issues.fields.summary");
    }

    public String getKeyForSummary(final String summary) {
        return jsonPath.getString(
                String.format("issues.find {it.fields.summary == '%s'}.key", summary));
    }
}
