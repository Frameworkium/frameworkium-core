package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.path.json.JsonPath;
import java.util.List;

public class Search extends AbstractJiraService {
  private static final int MAX_SEARCH_RESULTS = 1000;
  protected JsonPath searchResults;

  public Search(final String query) {
    this(query, 0, MAX_SEARCH_RESULTS);
  }

  public Search(final String query, final int startAt, final int maxSearchResults) {
    try {
      this.searchResults = getRequestSpec().log().ifValidationFails()
          .basePath(JiraEndpoint.SEARCH.getUrl())
          .param("jql", query)
          .param("startAt", startAt)
          .param("maxResults", maxSearchResults)
          .when()
          .get()
          .then().log().ifValidationFails()
          .extract().jsonPath();
    } catch (Exception e) {
      logger.error("Unable to search for JIRA issue", e);
    }
  }

  public List<String> getKeys() {
    return searchResults.getList("issues.key");
  }
}
