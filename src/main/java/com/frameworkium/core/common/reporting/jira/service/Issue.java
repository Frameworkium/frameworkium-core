package com.frameworkium.core.common.reporting.jira.service;

import com.frameworkium.core.common.reporting.jira.endpoint.JiraEndpoint;
import io.restassured.http.ContentType;
import org.json.*;

public class Issue extends AbstractJiraService {
    /**
     * Edit a field in a JIRA issue
     *
     * @param issueKey      the issue's key value
     * @param fieldToUpdate the issue's field to update. Only editable field can be updated.
     *                      Use /rest/api/2/issue/{issueIdOrKey}/editmeta to find out which
     * @param resultValue   the desired field value
     */
    public void editField(String issueKey, String fieldToUpdate, String resultValue) {
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
     * @param issueKey     the issue's key value
     * @param commentToAdd the comment to add
     */
    public void addComment(String issueKey, String commentToAdd) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("body", commentToAdd);
            getRequestSpec().log().ifValidationFails()
                    .basePath(JiraEndpoint.ISSUE.getUrl())
                    .contentType(ContentType.JSON)
                    .pathParam("issueKey", issueKey)
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
     * @param issueKey       the issue's key value
     * @param transitionName the name of the transition to perform on
     */
    public void transition(String issueKey, String transitionName) {
        transitionById(issueKey, getTransitionId(issueKey, transitionName));
    }

    private void transitionById(String issueKey, String transitionId) {
        JSONObject obj = new JSONObject();
        JSONObject idObj = new JSONObject();

        try {
            obj.put("transition", idObj);
            idObj.put("id", transitionId);
            getRequestSpec().log().ifValidationFails()
                    .basePath(JiraEndpoint.ISSUE.getUrl())
                    .contentType(ContentType.JSON).and()
                    .pathParam("issueKey", issueKey)
                    .body(obj.toString())
                    .when()
                    .post("/{issueKey}/transitions");
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for transition change", e);
        }
    }

    private String getTransitionId(String issueKey, String transitionName) {
        return getRequestSpec().log().ifValidationFails()
                .basePath(JiraEndpoint.ISSUE.getUrl())
                .pathParam("issueKey", issueKey)
                .queryParam("expand", "transitions.fields")
                .get("/{issueKey}/transitions").then().log().ifValidationFails()
                .extract().jsonPath()
                .getString(String.format(
                        "transitions.find {it -> it.name == '%s'}.id", transitionName));
    }
}

