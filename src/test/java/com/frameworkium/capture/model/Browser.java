package com.frameworkium.capture.model;

import com.frameworkium.config.SystemProperty;

public class Browser {
    private String name;
    private String version;
    private String device;
    private String platform;

    public Browser() {
        if (SystemProperty.BROWSER.isSpecified()) {
            this.name = SystemProperty.BROWSER.getValue().toLowerCase();
        }
        if (SystemProperty.BROWSER_VERSION.isSpecified()) {
            this.version = SystemProperty.BROWSER_VERSION.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
