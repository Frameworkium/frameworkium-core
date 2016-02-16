package com.frameworkium.ui.config;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

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
    SPIRA_URL("spiraURL"),
    RESULT_VERSION("resultVersion"),
    ZAPI_CYCLE_REGEX("zapiCycleRegEx"),
    JQL_QUERY("jqlQuery"),
    JIRA_USERNAME("jiraUsername"),
    JIRA_PASSWORD("jiraPassword"),
    SUT_NAME("sutName"),
    SUT_VERSION("sutVersion"),
    JIRA_RESULT_FIELDNAME("jiraResultFieldName"),
    JIRA_RESULT_TRANSITION("jiraResultTransition"),
    MAXIMISE("maximise"),
    RESOLUTION("resolution"),
    PROXY("proxy"),
    FIREFOX_PROFILE("firefoxProfile");

    private String value;
    private static Map configMap = null;

    SystemProperty(final String key) {
        this.value = retrieveValue(key);
    }

    public String getValue() {
        return this.value;
    }

    public boolean isSpecified() {
        return null != this.value && !this.value.isEmpty();
    }

    private String retrieveValue(final String key) {
        if (System.getProperty(key) != null) {
            return System.getProperty(key);
        }
        if (System.getProperty("config") != null && configMap == null) {
            configMap = (Map) new Yaml().load(ClassLoader.getSystemResourceAsStream(System.getProperty("config")));
        }
        if (configMap != null) {
            Object configValue = configMap.get(key);
            if (configValue != null) {
                return configValue.toString();
            }
        }
        return null;
    }
}
