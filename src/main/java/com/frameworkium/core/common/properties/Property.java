package com.frameworkium.core.common.properties;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

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
    JIRA_RESULT_FIELD_NAME("jiraResultFieldName"),
    JIRA_RESULT_TRANSITION("jiraResultTransition"),
    PROXY("proxy"),
    MAX_RETRY_COUNT("maxRetryCount"),
    // UI specific
    BROWSER("browser"),
    BROWSER_VERSION("browserVersion"),
    PLATFORM("platform"),
    PLATFORM_VERSION("platformVersion"),
    DEVICE("device"),
    CAPTURE_URL("captureURL"),
    GRID_URL("gridURL"),
    APP_PATH("appPath"),
    APPLICATION_NAME("applicationName"),
    SAUCE("sauce"),
    BROWSER_STACK("browserStack"),
    MAXIMISE("maximise"),
    RESOLUTION("resolution"),
    FIREFOX_PROFILE("firefoxProfile"),
    CHROME_USER_DATA_DIR("chromeUserDataDir");

    private static Map configMap = null;
    private String value;

    Property(String key) {
        this.value = retrieveValue(key);
    }

    private String retrieveValue(String key) {
        if (System.getProperty(key) != null) {
            return System.getProperty(key);
        } else {
            return getValueFromConfigFile(key);
        }
    }

    private String getValueFromConfigFile(String key) {
        if (configMap == null) {
            loadConfigFile();
        }

        if (configMap != null) {
            Object configValue = configMap.get(key);
            if (configValue != null) {
                return configValue.toString();
            }
        }
        return null;
    }

    private void loadConfigFile() {
        String configFileName = System.getProperty("config");
        if (StringUtils.isNotEmpty(configFileName)) {
            try {
                configMap = (Map) new Yaml().load(
                        ClassLoader.getSystemResourceAsStream(configFileName));
            } catch (YAMLException e) {
                throw new RuntimeException(
                        "Config file '" + configFileName + "' not found.", e);
            }
        }
    }

    /**
     * @return true iff the maximise property is equal, ignoring case, to "true"
     */
    public static boolean wantToMaximise() {
        return MAXIMISE.isSpecified()
                && Boolean.parseBoolean(MAXIMISE.getValue());
    }

    /**
     * @return true iff the property is not empty ("") and not null
     */
    public boolean isSpecified() {
        return StringUtils.isNotEmpty(value);
    }

    public String getValue() {
        return value;
    }

    /**
     * @return true if all the require properties for Capture are specified
     */
    public static boolean allCapturePropertiesSpecified() {
        return CAPTURE_URL.isSpecified()
                && SUT_NAME.isSpecified()
                && SUT_VERSION.isSpecified();
    }
}
