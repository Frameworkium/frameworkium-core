package com.frameworkium.core.common.reporting.spira;

public class SpiraConfig {

    public static final String USERNAME = "administrator";
    public static final String API_KEY = "{750AE393-737C-434E-A3AB-0FF2D0476E3C}";
    public static final String REST_PATH = "/Services/v4_0/RestService.svc/projects/2";

    public static class SpiraStatus {
        public static final int SPIRA_STATUS_PASS = 2;
        public static final int SPIRA_STATUS_FAIL = 1;
        public static final int SPIRA_STATUS_WIP = 4;
        public static final int SPIRA_STATUS_BLOCKED = 5;
    }

}
