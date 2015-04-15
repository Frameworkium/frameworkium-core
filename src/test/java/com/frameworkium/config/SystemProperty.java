package com.frameworkium.config;

public enum SystemProperty {

    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    GRID_URL("gridURL"),
    BUILD("build"),
    APP_PATH("appPath"),
    SAUCE("sauce"),
    BROWSER_STACK("browserStack"),
    JIRA_URL("jiraURL"),
    JIRA_RESULT_VERSION("jiraResultVersion"),
    JQL_QUERY("jqlQuery");

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
