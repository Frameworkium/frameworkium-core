package com.frameworkium.config;

public enum SystemProperty {

    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    CAPTURE_URL("captureURL"),
    GRID_URL("gridURL"),
    BUILD("build"),
    APP_PATH("appPath"),
    SAUCE("sauce"),
    BROWSER_STACK("browserStack"),
    JIRA_URL("jiraURL"),
    ZAPI_RESULT_VERSION("zapiResultVersion"),
    JIRA_CYCLE_REGEX("jiraCycleRegEx"),
    JQL_QUERY("jqlQuery"),
    SUT_NAME("sutName"),
    SUT_VERSION("sutVersion"),
    JIRA_RESULT_FIELDNAME("jiraResultFieldName"),
    JIRA_RESULT_TRANSITION("jiraResultTransition");


    private String value;

    private SystemProperty(String key) {
        this.value = System.getProperty(key);
    }

    public String getValue() {
        return value;
    }

    public boolean isSpecified() {
        return null != value && !value.isEmpty();
    }
}
