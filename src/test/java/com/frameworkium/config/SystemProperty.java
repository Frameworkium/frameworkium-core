package com.frameworkium.config;

public enum SystemProperty {

    BROWSER("browser"),
    BROWSER_VERSION("desiredBrowserVersion"),
    PLATFORM("desiredPlatform"),
    PLATFORM_VERSION("desiredPlatformVersion"),
    DEVICE_NAME("desiredDeviceName"),
    GRID_URL("gridURL"),
    BUILD("build"),
    APP_PATH("appPath"),
    SAUCE("sauce"),
    JIRA_RESULT_VERSION("jiraResultVersion");

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
