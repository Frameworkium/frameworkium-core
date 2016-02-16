package com.frameworkium.common.reporting.spira;

public class Config {

    public static final boolean FailTestOnlyIfAssertionError = true;

    public static final String spiraUsername = "administrator";
    public static final String spiraApiKey= "{750AE393-737C-434E-A3AB-0FF2D0476E3C}";

    public static final String spiraRestURI = "/Services/v4_0/RestService.svc/projects/2";

    public static class SPIRA_STATUS {
        public static final int SPIRA_STATUS_PASS = 2;
        public static final int SPIRA_STATUS_FAIL = 1;
        public static final int SPIRA_STATUS_WIP = 4;
        public static final int SPIRA_STATUS_BLOCKED = 5;
    }
    

}
