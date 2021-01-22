package com.frameworkium.core.common.reporting.jira.util;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.JiraConfig;
import com.frameworkium.core.common.reporting.jira.dto.attachment.AttachmentListDto;
import com.frameworkium.core.common.reporting.jira.dto.execution.*;
import com.frameworkium.core.common.reporting.jira.zapi.Attachment;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.testng.ITestResult;

import java.io.File;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExecutionUtil {

    private static final Logger logger = LogManager.getLogger();

    private final String version;
    private final String issue;
    private List<Integer> idList;
    private int currentStatus;
    private final ExecutionSearchUtil executionSearchUtil;
    private final Execution execution;
    private final Attachment attachment;

    /**
     * Constructor that links an execution to an issue with default version
     * from {@link com.frameworkium.core.common.properties.Property#RESULT_VERSION}.
     *
     * @param issue the target Issue to do operations on
     */
    public ExecutionUtil(String issue) {
        this(issue, Property.RESULT_VERSION.getValue());
    }

    /**
     * Constructor that links an execution to an issue
     *
     * @param issue   the target Issue to do operations on
     * @param version the target Jira fixVersion to filter Issues with
     */
    public ExecutionUtil(String issue, String version) {
        this(new ExecutionSearchUtil(String.format(
                "issue='%s' and fixVersion='%s'", issue, version)),
                new Execution(),
                new Attachment(),
                issue,
                version
        );
    }

    /**
     * Constructor that links an execution to an issue
     *
     * @param executionSearchUtil a Jira Execution Search Utility instance
     * @param execution           a ZAPI execution instance
     * @param attachment          a ZAPI attachment instance
     * @param issue               the target Issue to do operations on
     * @param version             the target Jira fixVersion to filter Issues with
     */
    public ExecutionUtil(final ExecutionSearchUtil executionSearchUtil,
                         final Execution execution, final Attachment attachment,
                         final String issue, final String version) {
        this.executionSearchUtil = executionSearchUtil;
        this.execution = execution;
        this.attachment = attachment;
        this.issue = issue;
        this.version = version;
        initExecutionIdsAndCurrentStatus();
    }

    private void initExecutionIdsAndCurrentStatus() {
        if (isBlank(version) || isBlank(issue)) {
            return;
        }
        idList = executionSearchUtil.getExecutionIdsByZAPICycleRegex();

        List<Integer> statusList = executionSearchUtil.getExecutionStatusesByZAPICycleRegex();
        if (!statusList.isEmpty()) {
            currentStatus = statusList.get(0);
        }
    }

    /**
     * Gets the ZAPI status of the TestNG status.
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
        if (idList == null) {
            return;
        }
        for (Integer executionId : idList) {
            updateStatusAndComment(executionId, status, comment);
            replaceExistingAttachments(executionId, attachments);

            logger.debug("ZAPI Updater - Updated {} to status {}", issue, status);
        }
    }

    private void updateStatusAndComment(Integer executionId, int status, String comment) {
        try {
            int commentMaxLen = 750;
            final UpdateExecutionOperationDto updateExecutionOperationDto = UpdateExecutionOperationDto.newBuilder()
                    .status(status)
                    .executionDto(ExecutionDto.newBuilder()
                            .executionLightDto(ExecutionLightDto.newBuilder()
                                    .comment(StringUtils.abbreviate(comment, commentMaxLen))
                                    .build())
                            .build())
                    .build();
            execution.updateExecutionDetails(updateExecutionOperationDto, executionId);
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
        final AttachmentListDto executionListDto = attachment.getAttachmentByEntity(executionId, "EXECUTION");
        executionListDto.data.stream()
                .map(a -> a.fileId)
                .map(Long::parseLong)
                .forEach(attachment::deleteAttachment);
    }

    private void addAttachments(Integer executionId, String... attachments) {
        Arrays.stream(attachments)
                .filter(Objects::nonNull)
                .map(File::new)
                .forEach(file ->
                        attachment.addAttachments(executionId, "EXECUTION", file));
    }
}
