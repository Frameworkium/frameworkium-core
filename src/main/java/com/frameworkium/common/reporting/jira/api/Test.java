package com.frameworkium.common.reporting.jira.api;

import static com.frameworkium.ui.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;

import com.frameworkium.reporting.jira.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;
import com.jayway.restassured.path.json.JsonPath;

public class Test {

    private final static AuthenticationScheme auth = preemptive().basic(Config.jiraUsername, Config.jiraPassword);
    private final static String jiraAPIURI = JIRA_URL.getValue() + Config.jiraRestURI;

    private final String issueKey; // Jira Key e.g. KT-123

    private final static Logger logger = LogManager.getLogger(Test.class);

    public Test(final String issue) {
        this.issueKey = issue;
    }

    public static void changeIssueFieldValue(final String issueKey, final String fieldToUpdate, final String resultValue) {
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

        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        given().contentType("application/json").and().body(obj.toString())
                .then().put("/issue/" + issueKey);
    }
    
    public static void addComment(String issueKey, String commentToAdd) {
        JSONObject obj = new JSONObject();
        
        try {
            obj.put("body", commentToAdd);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for comment update", e);
        }

        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        given().contentType("application/json").and().body(obj.toString())
                .then().post("/issue/" + issueKey + "/comment");
    }
    
    public static String getFieldId(String fieldName) {
        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;
        
        JsonPath jsonPath = get("/field").andReturn().jsonPath();

        return jsonPath.getString(String.format("find {it.name == '%s'}.id", fieldName));
    }

    public static int getTransitionId(String issueKey, String transitionName) {
        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;
        
        JsonPath jsonPath = get("/issue/" + issueKey + "?expand=transitions.fields")
                .andReturn().jsonPath();
        String jsonPathToTransitionId =String.format(
                "transitions.find {it -> it.name == '%s'}.id", transitionName);
        return Integer.parseInt(jsonPath.getString(jsonPathToTransitionId));
    }
    
    public static void transitionIssue(String issueKey, String transitionName) {
        logger.debug("Transition name: " + transitionName);
        transitionIssue(issueKey, getTransitionId(issueKey,transitionName));
    }
    
    public static void transitionIssue(String issueKey, int transitionId) {
        logger.debug("Transition id: " + transitionId);
        JSONObject obj = new JSONObject();
        JSONObject idObj = new JSONObject();
        
        try {
            obj.put("transition", idObj);
            idObj.put("id", transitionId);
        } catch (JSONException e) {
            logger.error("Can't create JSON Object for transition change", e);
        }

        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        given().contentType("application/json").and().body(obj.toString())
                .then().post("/issue/" + issueKey + "/transitions");
    }
}
