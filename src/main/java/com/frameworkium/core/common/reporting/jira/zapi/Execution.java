package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.File;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Execution {

    private final static Logger logger = LogManager.getLogger(Execution.class);
    private final static String zapiURI = Property.JIRA_URL.getValue() + Config.zapiRestURI;

    private final String version;
    private final String issue;
    private List<Integer> idList;
    private int status;

    public Execution(String issue) {
        this.version = Property.RESULT_VERSION.getValue();
        this.issue = issue;
        this.idList = getExecutionIds();
    }

    private List<Integer> getExecutionIds() {
        if (null == idList) {
            if (isNotEmpty(version) && isNotEmpty(issue)) {
                String query = String.format("issue='%s' and fixVersion='%s'", issue, version);

                SearchExecutions search = new SearchExecutions(query);
                idList = search.getExecutionIds();

                List<Integer> statusList = search.getExecutionStatuses();
                if (!statusList.isEmpty()) {
                    status = statusList.get(0);
                }
            }
        }
        return idList;
    }

    public int getExecutionStatus() {
        return status;
    }

    public void update(int status, String comment, String attachment) {
        if (null != idList) {
            for (Integer executionId : idList) {
                updateStatusAndComment(executionId, status, comment);
                replaceExistingAttachment(executionId, attachment);

                logger.debug("ZAPI Updater - Updated %s to status %s", issue, status);
            }
        }
    }

    private void replaceExistingAttachment(Integer executionId, String attachment) {
        if (isNotEmpty(attachment)) {
            deleteExistingAttachments(executionId);
            addAttachment(executionId, attachment);
        }
    }

    private void updateStatusAndComment(Integer executionId, int status, String comment) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("status", String.valueOf(status));
            // Limit on Zephyr's comment field capacity
            if (comment.length() > 750) {
                comment = comment.substring(0, 747) + "...";
            }
            obj.put("comment", comment);

            given().auth().preemptive()
                    .basic(Config.jiraUsername, Config.jiraPassword)
                    .contentType("application/json")
                    .body(obj.toString())
                    .then()
                    .put(zapiURI + "execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }
    }

    private void deleteExistingAttachments(Integer executionId) {

        String path = "attachment/attachmentsByEntity?entityType=EXECUTION&entityId=" + executionId;

        List<String> fileIds =
                given().auth().preemptive()
                        .basic(Config.jiraUsername, Config.jiraPassword)
                        .then()
                        .get(zapiURI + path).andReturn().jsonPath()
                        .getList("data.fileId", String.class);

        // Iterate over attachments
        for (String fileId : fileIds) {
            given().auth().preemptive()
                    .basic(Config.jiraUsername, Config.jiraPassword)
                    .then()
                    .delete(zapiURI + "attachment/" + fileId);
        }
    }

    private void addAttachment(Integer executionId, String attachment) {

        String path = "attachment?entityType=EXECUTION&entityId=" + executionId;

        given().auth().preemptive()
                .basic(Config.jiraUsername, Config.jiraPassword)
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(new File(attachment))
                .when()
                .post(zapiURI + path);
    }

    /** Converts ITestResult status to ZAPI execution status */
    public static int getZAPIStatus(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return Config.ZAPI_STATUS.ZAPI_STATUS_PASS;
            case ITestResult.FAILURE:
                return Config.ZAPI_STATUS.ZAPI_STATUS_FAIL;
            case ITestResult.SKIP:
                return Config.ZAPI_STATUS.ZAPI_STATUS_BLOCKED;
            default:
                return Config.ZAPI_STATUS.ZAPI_STATUS_FAIL;
        }
    }
}
