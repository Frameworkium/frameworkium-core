package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.reporting.jira.JiraConfig;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.JIRA_REST_PATH;

public class JiraTest {

    private static final Logger logger = LogManager.getLogger();

    /** Create and send a PUT request to JIRA to change the value of a field. */
    public static void changeIssueFieldValue(
            String issueKey, String fieldToUpdate, String resultValue) {

        JSONObject obj = new JSONObject();
        JSONObject fieldObj = new JSONObject();
        JSONObject setObj = new JSONObject();
        JSONArray setArr = new JSONArray();
        JSONObject valueObj = new JSONObject();

        try {
            obj.put("update", fieldObj);
            fieldObj.put(getFieldId(fieldToUpdate), setArr);
            setArr.put(setObj);
            setObj.put("set", valueObj);
            valueObj.put("value", resultValue);

        } catch (JSONException e) {
            logger.error("Can't create JSON Object for test case result update", e);
        }

        JiraConfig.getJIRARequestSpec()
                .contentType("application/json").and()
                .body(obj.toString())
                .when()
                .put(JIRA_REST_PATH + "/issue/" + issueKey);
    }

    private static String getFieldId(String fieldName) {

        JsonPath jsonPath = JiraConfig.getJIRARequestSpec()
                .when()
                .get(JIRA_REST_PATH + "/field")
                .thenReturn().jsonPath();

        return jsonPath.getString(String.format("find {it.name == '%s'}.id", fieldName));
    }

    /**
     * Create and post a JSON request for a comment update in JIRA.
     */
    public static void addComment(String issueKey, String commentToAdd) {

        JSONObject obj = new JSONObject();

        try {
            obj.put("body", commentToAdd);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for comment update", e);
        }

        JiraConfig.getJIRARequestSpec()
                .contentType("application/json")
                .body(obj.toString())
                .when()
                .post(JIRA_REST_PATH + "/issue/" + issueKey + "/comment");
    }

    /**
     * Create and post a JSON request for a transition change in JIRA.
     */
    public static void transitionIssue(String issueKey, String transitionName) {

        logger.debug("Transition name: " + transitionName);
        transitionIssue(issueKey, getTransitionId(issueKey, transitionName));
    }

    private static void transitionIssue(String issueKey, int transitionId) {

        logger.debug("Transition id: " + transitionId);
        JSONObject obj = new JSONObject();
        JSONObject idObj = new JSONObject();

        try {
            obj.put("transition", idObj);
            idObj.put("id", transitionId);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for transition change", e);
        }

        JiraConfig.getJIRARequestSpec()
                .contentType("application/json").and()
                .body(obj.toString())
                .when()
                .post(JIRA_REST_PATH + "issue/" + issueKey + "/transitions");
    }

    private static int getTransitionId(String issueKey, String transitionName) {

        final String jsonPathToTransitionId = String.format(
                "transitions.find {it -> it.name == '%s'}.id", transitionName);
        return JiraConfig.getJIRARequestSpec()
                .get(JIRA_REST_PATH + "/issue/" + issueKey + "?expand=transitions.fields")
                .thenReturn()
                .jsonPath()
                .getInt(jsonPathToTransitionId);
    }

}
