package com.frameworkium.capture.model;

import com.frameworkium.config.SystemProperty;

public class Browser {
    private String name;
    private String version;
    private String device;
    private String platform;
    private String platformVersion;

    public Browser() {
        if (SystemProperty.BROWSER.isSpecified()) {
            this.name = SystemProperty.BROWSER.getValue().toLowerCase();
        }
        if (SystemProperty.BROWSER_VERSION.isSpecified()) {
            this.version = SystemProperty.BROWSER_VERSION.getValue();
        }
        if (SystemProperty.DEVICE.isSpecified()) {
            this.device = SystemProperty.DEVICE.getValue();
        }
        if (SystemProperty.PLATFORM.isSpecified()) {
            this.platform = SystemProperty.PLATFORM.getValue();
        }
        if (SystemProperty.PLATFORM_VERSION.isSpecified()) {
            this.platformVersion = SystemProperty.PLATFORM_VERSION.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDevice() {
        return device;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }
}
