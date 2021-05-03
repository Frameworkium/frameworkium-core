package com.frameworkium.core.common.reporting.jira.service;

import java.util.List;

public class Attachment extends AbstractJiraService {
  private final Issue issue;

  public Attachment(Issue issue) {
    this.issue = issue;
  }

  /**
   * Returns list of attachment IDs.
   */
  public List<String> getIds() {
    return issue.getIssue().getList("fields.attachment.id");
  }
}

