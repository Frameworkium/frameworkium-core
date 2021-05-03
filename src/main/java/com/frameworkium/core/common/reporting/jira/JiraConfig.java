package com.frameworkium.core.common.reporting.jira;

public class JiraConfig {
  private JiraConfig() {
    // hide default constructor for this util class
  }

  /**
   * These should correspond to your ZAPI result IDs and
   * are only used if logging to Zephyr for JIRA.
   */
  public static class ZapiStatus {

    public static final int ZAPI_STATUS_PASS = 1;
    public static final int ZAPI_STATUS_FAIL = 2;
    public static final int ZAPI_STATUS_WIP = 3;
    public static final int ZAPI_STATUS_BLOCKED = 4;
  }

  /**
   * These should correspond to your field options
   * if logging a test result to a field.
   */
  public static class JiraFieldStatus {
    public static final String JIRA_STATUS_PASS = "Pass";
    public static final String JIRA_STATUS_FAIL = "Fail";
    public static final String JIRA_STATUS_WIP = "WIP";
    public static final String JIRA_STATUS_BLOCKED = "Blocked";
  }

  /**
   * These should correspond to the workflow transition names required to mark
   * the result if using a customised jira issue type & workflow to manage
   * tests NB - put all required transitions to get between statuses
   * (e.g. restart, then mark result) - each will be tried & ignored if not possible
   */
  public static class JiraTransition {
    public static final String[] JIRA_TRANSITION_PASS = {"Done"};
    public static final String[] JIRA_TRANSITION_FAIL = {"Done"};
    public static final String[] JIRA_TRANSITION_WIP = {"Reopen", "Start Progress"};
    public static final String[] JIRA_TRANSITION_BLOCKED = {"Done"};
  }

  /**
   * These should correspond to your field options
   * if logging a test result to a field.
   */
  public static class SpiraStatus {
    public static final int SPIRA_STATUS_PASS = 2;
    public static final int SPIRA_STATUS_FAIL = 1;
    public static final int SPIRA_STATUS_WIP = 4;
    public static final int SPIRA_STATUS_BLOCKED = 5;
  }

}
