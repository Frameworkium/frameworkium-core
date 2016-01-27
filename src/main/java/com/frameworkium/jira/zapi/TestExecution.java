package com.frameworkium.jira.zapi;

import com.frameworkium.jira.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static com.frameworkium.config.SystemProperty.JIRA_URL;
import static com.jayway.restassured.RestAssured.*;


public class TestExecution {

    final static Logger logger = LogManager.getLogger(TestExecution.class);
    final static String zapiURI = JIRA_URL.getValue() + Config.zapiRestURI;

    private String version;
    private String issue;
    private List<Integer> executionIdList;
    private int status;

    public TestExecution(final String version, final String issue) {
        this.version = version;
        this.issue = issue;
        this.executionIdList = getExecutionIds();
    }

    private List<Integer> getExecutionIds() {
        if (null == executionIdList) {
            if (null != version && !version.isEmpty() && null != issue && !issue.isEmpty()) {
                String query = String.format("issue='%s' and fixVersion='%s'", issue, version);

                SearchExecutions search = new SearchExecutions(query);
                executionIdList = search.getExecutionIds();

                List<Integer> statusList = search.getExecutionStatuses();
                if (!statusList.isEmpty()) {
                    status = statusList.get(0);
                }
            }
        }
        return executionIdList;
    }

    public void update(final int status, final String comment, final String attachment) {
        if (null != executionIdList) {
            for (Integer executionId : executionIdList) {
                updateStatusAndComment(executionId, status, comment);
                replaceExistingAttachment(attachment, executionId);

                logger.debug("ZAPI Updater - Updated %s to status %s", issue, status);
            }
        }
    }

    private void updateStatusAndComment(final Integer executionId, final int status, String comment) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("status", String.valueOf(status));
            //Limit on Zephyr's comment field capacity
            if (comment.length() > 750) {
                comment = comment.substring(0, 747) + "...";
            }
            obj.put("comment", comment);

            given().contentType("application/json")
                    .auth().preemptive().basic(Config.jiraUsername, Config.jiraPassword)
                    .baseUri(zapiURI)
                    .and().body(obj.toString())
                    .put("execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }
    }

    public int getExecutionStatus() {
        return status;
    }

    private void replaceExistingAttachment(final String attachment, final Integer executionId) {
        if (null != attachment && !attachment.isEmpty()) {
            deleteExistingAttachments(executionId);
            addAttachment(executionId, attachment);
        }
    }

    private void deleteExistingAttachments(final Integer executionId) {

        String url = "attachment/attachmentsByEntity?entityType=EXECUTION&entityId=" + executionId;
        List<String> fileIds = get(url).andReturn().jsonPath().getList("data.fileId", String.class);

        // Iterate over attachments
        for (String fileId : fileIds) {
            delete("attachment/" + fileId);
        }
    }

    private void addAttachment(final Integer executionId, final String attachment) {

        String url = "attachment?entityType=EXECUTION&entityId=" + executionId;
        given().header("X-Atlassian-Token", "nocheck").and().multiPart(new File(attachment)).when().post(url);
    }
}



