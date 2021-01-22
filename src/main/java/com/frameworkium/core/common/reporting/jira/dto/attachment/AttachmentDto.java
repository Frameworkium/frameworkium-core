package com.frameworkium.core.common.reporting.jira.dto.attachment;

import com.frameworkium.core.api.dto.AbstractDTO;

public class AttachmentDto extends AbstractDTO<AttachmentDto> {
    public String fileName;
    public String dateCreated;
    public String fileSize;
    public String fileIcon;
    public String author;
    public String fileIconAltText;
    public String comment;
    public String fileId;
}
