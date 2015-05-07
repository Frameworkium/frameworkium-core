package com.frameworkium.jira;

public class Config {

    public static final boolean FailTestOnlyIfAssertionError = true;

    public static final String jiraUsername = "automation";
    public static final String jiraPassword = "password";

    public static final String jiraRestURI = "/rest/api/latest";
    public static final String zapiRestURI = "/rest/zapi/latest";

    public static class ZAPI_STATUS {
        //These should correspond to your ZAPI result IDs
        //and are only used if logging to Zephyr for JIRA
        public static final int ZAPI_STATUS_PASS = 1;
        public static final int ZAPI_STATUS_FAIL = 2;
        public static final int ZAPI_STATUS_WIP = 3;
        public static final int ZAPI_STATUS_BLOCKED = 4;
    }
    
    public static class JIRA_FIELD_STATUS {
        //These should correspond to your field options if logging a test result to a field
        public static final String JIRA_STATUS_PASS = "Pass";
        public static final String JIRA_STATUS_FAIL = "Fail";
        public static final String JIRA_STATUS_WIP = "WIP";
        public static final String JIRA_STATUS_BLOCKED = "Blocked";
    }
    
    public static class JIRA_TRANSITION {
        //These should correspond to the workflow transition names required to mark the result if
        //using a customised jira issue type & workflow to manage tests
        //NB - put all required transitions to get between statuses (Eg restart, then mark result)
        // - each will be tried & ignored if not possible
        public static final String[] JIRA_TRANSITION_PASS = { "Done" };
        public static final String[] JIRA_TRANSITION_FAIL = { "Done" };
        public static final String[] JIRA_TRANSITION_WIP = {"Reopen", "Start Progress"};
        public static final String[] JIRA_TRANSITION_BLOCKED = { "Done" };
    }
    

}
