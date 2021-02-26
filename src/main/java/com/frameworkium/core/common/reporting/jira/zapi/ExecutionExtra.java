package com.frameworkium.core.common.reporting.jira.zapi;

import com.frameworkium.core.common.reporting.jira.dto.attachment.AttachmentListDto;
import com.frameworkium.core.common.reporting.jira.dto.execution.*;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

@AutoFactory
public class ExecutionExtra {
    private static final String EXECUTION = "EXECUTION";
    private Execution execution;
    private Attachment attachment;
    private Integer executionId;


    @Inject
    ExecutionExtra(@Provided Attachment attachment, @Provided Execution execution, final Integer executionId) {
        this.attachment = attachment;
        this.execution = execution;
        this.executionId = executionId;
    }

    public void updateComment(String comment) {
        int commentMaxLen = 750;
        final UpdateExecutionOperationDto updateExecutionOperationDto = UpdateExecutionOperationDto.newBuilder()
                .executionDto(ExecutionDto.newBuilder()
                        .executionLightDto(ExecutionLightDto.newBuilder()
                                .comment(StringUtils.abbreviate(comment, commentMaxLen))
                                .build())
                        .build())
                .build();
        execution.updateExecutionDetails(updateExecutionOperationDto, executionId);
    }

    public void updateStatus(Integer status) {
        final UpdateExecutionOperationDto updateExecutionOperationDto = UpdateExecutionOperationDto.newBuilder()
                .status(status)
                .build();
        execution.updateExecutionDetails(updateExecutionOperationDto, executionId);
    }

    public void deleteExistingAttachments() {
        final AttachmentListDto executionListDto = attachment.getAttachmentByEntity(executionId, EXECUTION);
        executionListDto.data.stream()
                .map(a -> a.fileId)
                .map(Long::parseLong)
                .forEach(attachment::deleteAttachment);
    }

    public void addAttachments(String... attachments) {
        Arrays.stream(attachments)
                .filter(Objects::nonNull)
                .map(File::new)
                .forEach(file ->
                        attachment.addAttachments(executionId, EXECUTION, file));
    }
}
