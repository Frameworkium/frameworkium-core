package com.frameworkium.core.common.properties;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public enum Property {

    BUILD("build"),
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
    PROXY("proxy"),
    // UI specific
    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    CAPTURE_URL("captureURL"),
    GRID_URL("gridURL"),
    APP_PATH("appPath"),
    SAUCE("sauce"),
    BROWSER_STACK("browserStack"),
    MAXIMISE("maximise"),
    RESOLUTION("resolution"),
    FIREFOX_PROFILE("firefoxProfile");

    private String value;
    private static Map configMap = null;

    Property(final String key) {
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
        if (configMap == null && System.getProperty("config") != null) {
            configMap = (Map) new Yaml()
                    .load(ClassLoader.getSystemResourceAsStream(
                            System.getProperty("config")));
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
