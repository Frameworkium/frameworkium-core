package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.path.json.JsonPath;

import java.util.List;

public class SearchService extends AbstractJiraService {
    private static final int MAX_SEARCH_RESULTS = 1000;
    protected JsonPath searchResults;

    public SearchService(final String query) {
        this(query, 0, MAX_SEARCH_RESULTS);
    }

    public SearchService(final String query, final int startAt, final int maxSearchResults) {
        this.searchResults = getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.SEARCH.getUrl())
                .param("jql", query)
                .param("startAt", startAt)
                .param("maxResults", maxSearchResults)
                .when()
                .get()
                .then().log().ifValidationFails()
                .extract().jsonPath();
    }

    public List<String> getKeys() {
        return searchResults.getList("issues.key");
    }
}
