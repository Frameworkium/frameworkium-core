package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.jayway.restassured.RestAssured.given;

public class Test {

    private final static String jiraAPIURI = Property.JIRA_URL.getValue() + Config.jiraRestURI;
    private final static Logger logger = LogManager.getLogger(Test.class);

    public static void changeIssueFieldValue(String issueKey, String fieldToUpdate, String resultValue) {
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

        given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword).and()
                .contentType("application/json").and()
                .body(obj.toString())
                .then()
                .put(jiraAPIURI + "/issue/" + issueKey);
    }

    private static String getFieldId(String fieldName) {

        JsonPath jsonPath = given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .then()
                .get(jiraAPIURI + "/field")
                .andReturn().jsonPath();

        return jsonPath.getString(String.format("find {it.name == '%s'}.id", fieldName));
    }

    public static void addComment(String issueKey, String commentToAdd) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("body", commentToAdd);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for comment update", e);
        }

        given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword).and()
                .contentType("application/json").and()
                .body(obj.toString())
                .then()
                .post(jiraAPIURI + "/issue/" + issueKey + "/comment");
    }

    private static int getTransitionId(String issueKey, String transitionName) {

        JsonPath jsonPath = given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .then()
                .get(jiraAPIURI + "/issue/" + issueKey + "?expand=transitions.fields")
                .andReturn().jsonPath();

        String jsonPathToTransitionId = String.format(
                "transitions.find {it -> it.name == '%s'}.id", transitionName);
        return Integer.parseInt(jsonPath.getString(jsonPathToTransitionId));
    }

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

        given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword).and()
                .contentType("application/json").and()
                .body(obj.toString())
                .then()
                .post(jiraAPIURI + "issue/" + issueKey + "/transitions");
    }
}
