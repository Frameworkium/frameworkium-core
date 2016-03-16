package com.frameworkium.core.ui.properties;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public enum UIProperty {

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

    UIProperty(final String key) {
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
