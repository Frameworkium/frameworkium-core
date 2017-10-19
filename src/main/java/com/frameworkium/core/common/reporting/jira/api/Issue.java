package com.frameworkium.core.common.reporting.jira.api;

import com.frameworkium.core.common.reporting.jira.JiraConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class Issue {

    private static final Logger logger = LogManager.getLogger();
    private final String issueKey; // Jira Key e.g. KT-123

    public Issue(String issue) {
        this.issueKey = issue;
    }

    /**
     * Create and post a JSON request to JIRA to get issues.
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

        JiraConfig.getJIRARequestSpec()
                .contentType("application/json")
                .body(obj.toString())
                .when()
                .post(JiraConfig.JIRA_REST_PATH + "issueLink");
    }

    /** Returns list of attachment IDs. */
    public List<String> getAttachmentIds() {

        return JiraConfig.getJIRARequestSpec()
                .when()
                .get(JiraConfig.JIRA_REST_PATH + "issue/" + issueKey)
                .thenReturn().jsonPath()
                .getList("fields.attachment.id");
    }

    /** Adds the file attachment to the JIRA issue. */
    public void addAttachment(File attachment) {
        String url = String.format("issue/%s/attachments", issueKey);

        JiraConfig.getJIRARequestSpec()
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(attachment).and()
                .when()
                .post(JiraConfig.JIRA_REST_PATH + url)
                .thenReturn().statusLine();
    }
}
