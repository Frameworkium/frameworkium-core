package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Execution {

    private final static Logger logger = LogManager.getLogger();

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
                String query = String.format(
                        "issue='%s' and fixVersion='%s'", issue, version);

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

                logger.debug("ZAPI Updater - Updated {} to status {}", issue, status);
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
            int commentMaxLen = 750;
            obj.put("comment", StringUtils.abbreviate(comment, commentMaxLen));

            Config.getJIRARequestSpec()
                    .contentType("application/json")
                    .body(obj.toString())
                    .when()
                    .put(Config.zapiRestURI + "execution/" + executionId + "/execute");

        } catch (JSONException e) {
            logger.error("Update status and comment failed", e);
        }
    }

    private void deleteExistingAttachments(Integer executionId) {

        String path = "attachment/attachmentsByEntity?entityType=EXECUTION&entityId=" + executionId;

        List<String> fileIds =
                Config.getJIRARequestSpec()
                        .when()
                        .get(Config.zapiRestURI + path).thenReturn().jsonPath()
                        .getList("data.fileId", String.class);

        // Iterate over attachments
        fileIds.forEach(fileId ->
                Config.getJIRARequestSpec()
                        .when()
                        .delete(Config.zapiRestURI + "attachment/" + fileId)
        );
    }

    private void addAttachment(Integer executionId, String attachment) {

        String path = "attachment?entityType=EXECUTION&entityId=" + executionId;

        Config.getJIRARequestSpec()
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(new File(attachment))
                .when()
                .post(Config.zapiRestURI + path);
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
