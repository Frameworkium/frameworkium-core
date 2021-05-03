package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;

public class IssueLink extends AbstractJiraService {
  /**
   * Creates an issue link between two issues.
   *
   * @param type         name of issue
   * @param inwardIssue  inward issue key
   * @param outwardIssue outward issue key
   */
  public void linkIssues(String type, String inwardIssue, String outwardIssue) {
    JSONObject obj = new JSONObject();
    JSONObject typeObj = new JSONObject();
    JSONObject inwardIssueObj = new JSONObject();
    JSONObject outwardIssueObj = new JSONObject();

    try {
      obj.put("type", typeObj);
      typeObj.put("name", type);
      obj.put("inwardIssue", inwardIssueObj);
      inwardIssueObj.put("key", inwardIssue);
      obj.put("outwardIssue", outwardIssueObj);
      outwardIssueObj.put("key", outwardIssue);
    } catch (JSONException e) {
      logger.error("Can't create JSON Object for linkIssues", e);
    }

    getRequestSpec().log().ifValidationFails()
        .basePath(JiraEndpoint.ISSUELINK.getUrl())
        .contentType(ContentType.JSON)
        .body(obj.toString())
        .when()
        .post();
  }
}
