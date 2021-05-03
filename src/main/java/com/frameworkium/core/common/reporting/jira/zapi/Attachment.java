package com.frameworkium.core.common.reporting.jira.zapi;

import static org.apache.http.HttpStatus.SC_OK;

import com.frameworkium.core.common.reporting.jira.dto.attachment.AttachmentListDto;
import com.frameworkium.core.common.reporting.jira.endpoint.ZephyrEndpoint;
import com.frameworkium.core.common.reporting.jira.service.AbstractJiraService;
import java.io.File;

public class Attachment extends AbstractJiraService {
  public AttachmentListDto getAttachmentByEntity(Integer entityId, String entityType) {
    return getRequestSpec()
        .basePath(ZephyrEndpoint.ATTACHMENT_BY_ENTITY.getUrl())
        .queryParam("entityId", entityId)
        .queryParam("entityType", entityType)
        .when()
        .get()
        .then().log().ifValidationFails()
        .extract()
        .as(AttachmentListDto.class);
  }

  public void deleteAttachment(Long id) {
    getRequestSpec()
        .basePath(ZephyrEndpoint.ATTACHMENT.getUrl())
        .pathParam("id", id)
        .when()
        .delete("/{id}")
        .then().log().ifValidationFails()
        .statusCode(SC_OK);
  }

  public void addAttachments(Integer entityId, String entityType, File file) {
    getRequestSpec()
        .basePath(ZephyrEndpoint.ATTACHMENT.getUrl())
        .queryParam("entityId", entityId)
        .queryParam("entityType", entityType)
        .header("X-Atlassian-Token", "nocheck")
        .multiPart(file)
        .when()
        .post()
        .then().log().ifValidationFails()
        .statusCode(SC_OK);
  }
}
