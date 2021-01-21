package com.frameworkium.core.common.reporting.jira.dto.attachment;

import com.frameworkium.core.api.dto.AbstractDTO;

import java.util.List;

public class AttachmentListDto extends AbstractDTO<AttachmentListDto> {
    public List<AttachmentDto> data;
}
