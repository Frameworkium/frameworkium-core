package com.lazerycode.selenium.config;

public enum SystemProperty {

    BROWSER("browser"), BROWSER_VERSION("desiredBrowserVersion"), PLATFORM("desiredPlatform"), PLATFORM_VERSION("desiredPlatformVersion"), DEVICE_NAME(
            "desiredDeviceName"), GRID_URL("gridURL"), SAUCE_USER("SAUCE_USER_NAME"), SAUCE_KEY(
            "SAUCE_ACCESS_KEY"), ;

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
