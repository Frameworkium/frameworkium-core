package com.frameworkium.core.common.properties;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public enum CommonProperty {

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
    PROXY("proxy");

    private String value;
    private static Map configMap = null;

    CommonProperty(final String key) {
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
