package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.reporting.jira.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class Issue {

    private final String issueKey; // Jira Key e.g. KT-123
    private final static Logger logger = LogManager.getLogger(Issue.class);

    public Issue(String issue) {
        this.issueKey = issue;
    }

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

        Config.getJIRARequestSpec()
                .contentType("application/json")
                .body(obj.toString())
                .when()
                .post(Config.jiraRestURI + "issueLink");
    }

    public List<String> getAttachmentIds() {

        return Config.getJIRARequestSpec()
                .when()
                .get(Config.jiraRestURI + "issue/" + issueKey)
                .thenReturn().jsonPath()
                .getList("fields.attachment.id");
    }

    public void addAttachment(File attachment) {
        String url = String.format("issue/%s/attachments", issueKey);

        Config.getJIRARequestSpec()
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(attachment).and()
                .when()
                .post(Config.jiraRestURI + url)
                .thenReturn().statusLine();
    }
}
