package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.json.*;

import java.io.File;

public class Issue extends AbstractJiraService {
    public final String issueKey;
    private static final String ISSUE_KEY = "issueKey";

    public Issue(String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * Returns a full representation of the issue for the given issue key.
     *
     * @return the full representation of the issue as JsonPath
     */
    public JsonPath getIssue() {
        return getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.ISSUE.getUrl())
                .when()
                .get(issueKey)
                .then().log().ifValidationFails()
                .extract().jsonPath();
    }

    /**
     * Edit a field in a JIRA issue
     *
     * @param fieldToUpdate the issue's field to update. Only editable field can be updated.
     *                      Use /rest/api/2/issue/{issueIdOrKey}/editmeta to find out which
     * @param resultValue   the desired field value
     */
    public void editField(String fieldToUpdate, String resultValue) {
        JSONObject obj = new JSONObject();
        JSONObject fieldObj = new JSONObject();
        JSONArray setArr = new JSONArray();
        JSONObject setObj = new JSONObject();
        JSONObject valueObj = new JSONObject();

        try {
            obj.put("update", fieldObj);
            fieldObj.put(getFieldId(fieldToUpdate), setArr);
            setArr.put(setObj);
            setObj.put("set", valueObj);
            valueObj.put("value", resultValue);

            getRequestSpec().log().ifValidationFails()
                    .basePath(JiraEndpoint.ISSUE.getUrl())
                    .contentType(ContentType.JSON).and()
                    .body(obj.toString())
                    .when()
                    .put(issueKey)
                    .then().log().ifValidationFails();
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for test case result update", e);
        }
    }

    private String getFieldId(String fieldName) {
        return getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.FIELD.getUrl())
                .contentType(ContentType.JSON).and()
                .when()
                .get()
                .then().log().ifValidationFails()
                .extract().jsonPath()
                .getString(String.format("find {it.name == '%s'}.id", fieldName));
    }

    /**
     * Add comment into a JIRA issue
     *
     * @param commentToAdd the comment to add
     */
    public void addComment(String commentToAdd) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("body", commentToAdd);
            getRequestSpec().log().ifValidationFails()
                    .basePath(JiraEndpoint.ISSUE.getUrl())
                    .contentType(ContentType.JSON)
                    .pathParam(ISSUE_KEY, issueKey)
                    .body(obj.toString())
                    .when()
                    .post("/{issueKey}/comment");
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for comment update", e);
        }
    }

    /**
     * Perform a transition on an issue. When performing the transition you can update or set other issue fields.
     *
     * @param transitionName the name of the transition to perform on
     */
    public void transition(String transitionName) {
        transitionById(getTransitionId(transitionName));
    }

    private void transitionById(String transitionId) {
        JSONObject obj = new JSONObject();
        JSONObject idObj = new JSONObject();

        try {
            obj.put("transition", idObj);
            idObj.put("id", transitionId);
            getRequestSpec().log().ifValidationFails()
                    .basePath(JiraEndpoint.ISSUE.getUrl())
                    .contentType(ContentType.JSON).and()
                    .pathParam(ISSUE_KEY, issueKey)
                    .body(obj.toString())
                    .when()
                    .post("/{issueKey}/transitions");
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for transition change", e);
        }
    }

    private String getTransitionId(String transitionName) {
        return getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.ISSUE.getUrl())
                .pathParam(ISSUE_KEY, issueKey)
                .queryParam("expand", "transitions.fields")
                .get("/{issueKey}/transitions").then().log().ifValidationFails()
                .extract().jsonPath()
                .getString(String.format(
                        "transitions.find {it -> it.name == '%s'}.id", transitionName));
    }

    /**
     * Add one or more attachments to an issue.
     *
     * @param attachment The file to attach
     */
    public void addAttachment(File attachment) {
        getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.ISSUE.getUrl())
                .pathParam(ISSUE_KEY, issueKey)
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(attachment).and()
                .when()
                .post("/{issueKey}/attachments").then().log().ifValidationFails()
                .extract().statusLine();
    }
}

