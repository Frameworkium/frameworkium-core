package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.properties.CommonProperty;
import com.frameworkium.core.common.reporting.jira.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

public class Issue {

    private final static String jiraAPIURI = CommonProperty.JIRA_URL.getValue() + Config.jiraRestURI;
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

        given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword).and()
                .contentType("application/json").and()
                .body(obj.toString())
                .then()
                .post(jiraAPIURI + "issueLink");
    }

    public List<String> getAttachmentIds() {

        return given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                .then()
                .get(jiraAPIURI + "issue/" + issueKey).andReturn().jsonPath().getList("fields.attachment.id");

    }

    public void addAttachment(final File attachment) {
        String url = String.format("issue/%s/attachments", issueKey);

        given().auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword).and()
                .header("X-Atlassian-Token", "nocheck").and()
                .multiPart(attachment).and()
                .then()
                .post(jiraAPIURI + url)
                .andReturn().statusLine();

    }
}
