package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.File;
import java.util.*;

import static com.frameworkium.core.common.reporting.jira.JiraConfig.REST_ZAPI_PATH;
import static com.frameworkium.core.common.reporting.jira.JiraConfig.getJIRARequestSpec;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Execution {

    private static final Logger logger = LogManager.getLogger();

    private final String version;
    private final String issue;
    private List<Integer> idList;
    private int currentStatus;

    /**
     * Constructor that links an execution to an issue.
     */
    public Execution(String issue) {
        this.version = Property.RESULT_VERSION.getValue();
        this.issue = issue;
        initExecutionIdsAndCurrentStatus();
    }

    private void initExecutionIdsAndCurrentStatus() {
        if (isNotEmpty(version) && isNotEmpty(issue)) {
            String query = String.format(
                    "issue='%s' and fixVersion='%s'", issue, version);

            SearchExecutions search = new SearchExecutions(query);
            idList = search.getExecutionIds();

            List<Integer> statusList = search.getExecutionStatuses();
            if (!statusList.isEmpty()) {
                currentStatus = statusList.get(0);
            }
        }
    }

    /**
     * Gets the status of the status.
     *
     * @return ZAPI execution status from the ITestResult status
     */
    public static int getZAPIStatus(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_PASS;
            case ITestResult.FAILURE:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
            case ITestResult.SKIP:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_BLOCKED;
            default:
                return JiraConfig.ZapiStatus.ZAPI_STATUS_FAIL;
        }
    }

    public int getExecutionStatus() {
        return currentStatus;
    }

    /**
     * Update issue with a comment and attachments.
     */
    public void update(int status, String comment, String... attachments) {
        if (null != idList) {
            for (Integer executionId : idList) {
                updateStatusAndComment(executionId, status, comment);
                replaceExistingAttachments(executionId, attachments);

                logger.debug("ZAPI Updater - Updated {} to status {}", issue, status);
            }
        }
    }

    private void updateStatusAndComment(Integer executionId, int status, String comment) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("status", String.valueOf(status));
            int commentMaxLen = 750;
            obj.put("comment", StringUtils.abbreviate(comment, commentMaxLen));

            getJIRARequestSpec()
                    .contentType("application/json")
                    .body(obj.toString())
                    .when()
                    .put(REST_ZAPI_PATH + "execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }
    }

    private void replaceExistingAttachments(Integer executionId, String... attachments) {
        if (attachments != null) {
            deleteExistingAttachments(executionId);
            addAttachments(executionId, attachments);
        }
    }

    private void deleteExistingAttachments(Integer executionId) {

        String path = "attachment/attachmentsByEntity?entityType=EXECUTION&entityId=" + executionId;

        getJIRARequestSpec()
                .get(REST_ZAPI_PATH + path).thenReturn().jsonPath()
                .getList("data.fileId", String.class)
                .stream()
                .map(fileId -> REST_ZAPI_PATH + "attachment/" + fileId)
                .forEach(getJIRARequestSpec()::delete);
    }

    private void addAttachments(Integer executionId, String... attachments) {

        String path = REST_ZAPI_PATH
                + "attachment?entityType=EXECUTION&entityId=" + executionId;

        Arrays.stream(attachments)
                .filter(Objects::nonNull)
                .map(File::new)
                .forEach(attachment ->
                        getJIRARequestSpec()
                                .header("X-Atlassian-Token", "nocheck")
                                .multiPart(attachment)
                                .when()
                                .post(path));
    }
}
