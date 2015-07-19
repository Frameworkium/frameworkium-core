package com.frameworkium.jira.api;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.frameworkium.jira.Config;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.AuthenticationScheme;

public class Issue {

    private final static AuthenticationScheme auth = preemptive().basic(Config.jiraUsername, Config.jiraPassword);
    private final static String jiraAPIURI = JIRA_URL.getValue() + Config.jiraRestURI;

    private final String issueKey; // Jira Key e.g. KT-123

    private final static Logger logger = LogManager.getLogger(Issue.class);

    public Issue(final String issue) {
        this.issueKey = issue;
    }

    public static void linkIssues(final String type, final String inwardIssue, final String outwardIssue) {
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

        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        given().contentType("application/json").and().body(obj.toString()).then().post("issueLink");
    }

    public List<String> getAttachmentIds() {
        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        return get("issue/" + issueKey).andReturn().jsonPath().getList("fields.attachment.id");
    }

    public void addAttachment(final File attachment) {
        String url = String.format("issue/%s/attachments", issueKey);

        RestAssured.baseURI = jiraAPIURI;
        RestAssured.authentication = auth;

        given().header("X-Atlassian-Token", "nocheck").and().multiPart(attachment).and().log().all().when().post(url)
                .andReturn().statusLine();
    }
}
